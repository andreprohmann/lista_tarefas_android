package com.example.mypersonalorganizerv2.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.AuthViewModel

// TELA DE LOGIN.
// Não usa o MainScaffold, por isso NÃO tem menu (requisito do trabalho).
// A tela só mostra os campos; quem confere o login é o AuthViewModel.
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,          // chamado quando o login dá certo
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,    // abre a tela de cadastro
    viewModel: AuthViewModel = viewModel() // viewModel() cria (ou reaproveita) o ViewModel
) {
    // Controla se a senha aparece ou fica escondida (••••)
    var passwordVisible by remember { mutableStateOf(false) }

    // LaunchedEffect = "assim que a tela abrir, faça isso".
    // Se já existe uma sessão salva (usuário não clicou em Sair), pula direto para o app.
    LaunchedEffect(viewModel.isLoggedIn()) {
        if (viewModel.isLoggedIn()) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = PrimaryBlue) {}
        }

        Text(text = "Assessor Pessoal", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(text = "Seu assessor de confiança", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))

        // Campo de e-mail: o valor fica guardado no ViewModel
        CustomTextField(
            value = viewModel.email,
            onValueChange = {
                viewModel.email = it
                viewModel.clearError()
            },
            label = "E-mail",
            placeholder = "digite seu e-mail",
            leadingIcon = Icons.Default.Email,
            isError = viewModel.errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de senha, com o "olho" para mostrar/esconder
        CustomTextField(
            value = viewModel.password,
            onValueChange = {
                viewModel.password = it
                viewModel.clearError()
            },
            label = "Senha",
            placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = viewModel.errorMessage != null
        )

        // Se o ViewModel tiver uma mensagem de erro, mostra em vermelho
        viewModel.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botão ENTRAR: pede ao ViewModel para fazer o login; se der certo, abre o app
        PrimaryButton(text = "Entrar", onClick = {
            if (viewModel.login()) onLoginSuccess()
        })

        TextButton(onClick = onForgotPasswordClick) { Text("Esqueci minha senha", color = PrimaryBlue) }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onCreateAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(PrimaryBlue))
        ) {
            Text("Criar conta", color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
    }
}
