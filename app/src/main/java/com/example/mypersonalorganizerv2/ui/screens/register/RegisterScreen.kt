package com.example.mypersonalorganizerv2.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// TELA DE CADASTRO (criar conta).
// Também não usa o MainScaffold, então NÃO tem menu (requisito do trabalho).
// Os campos ficam guardados no AuthViewModel, que valida e salva no banco.
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,       // chamado quando a conta é criada
    onBackToLoginClick: () -> Unit,      // volta para o login
    viewModel: AuthViewModel = viewModel()
) {
    // Controla se a senha aparece ou fica escondida
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Criar conta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(32.dp))

        // Cada campo lê e grava o valor direto no ViewModel
        CustomTextField(value = viewModel.name, onValueChange = { viewModel.name = it; viewModel.clearError() }, label = "Nome completo", placeholder = "digite seu nome", leadingIcon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = viewModel.email, onValueChange = { viewModel.email = it; viewModel.clearError() }, label = "E-mail", placeholder = "digite seu e-mail", leadingIcon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = viewModel.phone, onValueChange = { viewModel.phone = it }, label = "Telefone", placeholder = "(00) 00000-0000", leadingIcon = Icons.Default.Phone)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = viewModel.birthDate, onValueChange = { viewModel.birthDate = it }, label = "Data de nascimento", placeholder = "dd/mm/aaaa", leadingIcon = Icons.Default.CalendarToday)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = viewModel.password, onValueChange = { viewModel.password = it; viewModel.clearError() }, label = "Senha", placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        // Mostra o erro de validação (ex.: "Já existe uma conta com este e-mail.")
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

        // Botão CADASTRAR: o ViewModel valida e salva; se der certo, volta para o login
        PrimaryButton(text = "Cadastrar", onClick = {
            if (viewModel.register()) onRegisterSuccess()
        })
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Já tem uma conta? ")
            TextButton(onClick = onBackToLoginClick) { Text("Entrar", color = PrimaryBlue, fontWeight = FontWeight.Bold) }
        }
    }
}
