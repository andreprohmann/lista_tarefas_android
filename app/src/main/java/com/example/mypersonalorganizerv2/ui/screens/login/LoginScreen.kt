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
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun validateAndLogin() {
        val trimmedUser = email.trim()
        val isValidUser = trimmedUser.equals("Admin", ignoreCase = true) ||
                trimmedUser.equals("admin@admin.com", ignoreCase = true)
        val isValidPassword = password == "admin"

        if (isValidUser && isValidPassword) {
            errorMessage = null
            onLoginClick()
        } else {
            errorMessage = "Usuário ou senha incorretos."
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

        CustomTextField(
            value = email,
            onValueChange = {
                email = it
                if (errorMessage != null) errorMessage = null
            },
            label = "E-mail ou Usuário",
            placeholder = "digite seu e-mail ou Admin",
            leadingIcon = Icons.Default.Email,
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                if (errorMessage != null) errorMessage = null
            },
            label = "Senha",
            placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock,
          //  trailingIcon = {
            //    IconButton(onClick = { passwordVisible = !passwordVisible }) {
              //      Icon(
                //        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                 //       contentDescription = null
                  //  )
                //}
            //},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = errorMessage != null
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(text = "Entrar", onClick = { validateAndLogin() })

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
