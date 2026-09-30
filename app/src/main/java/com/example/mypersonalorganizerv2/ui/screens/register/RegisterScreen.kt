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
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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

        CustomTextField(value = name, onValueChange = { name = it }, label = "Nome completo", placeholder = "digite seu nome", leadingIcon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = email, onValueChange = { email = it }, label = "E-mail", placeholder = "digite seu e-mail", leadingIcon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = phone, onValueChange = { phone = it }, label = "Telefone", placeholder = "(00) 00000-0000", leadingIcon = Icons.Default.Phone)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(value = birthDate, onValueChange = { birthDate = it }, label = "Data de nascimento", placeholder = "dd/mm/aaaa", leadingIcon = Icons.Default.CalendarToday)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password, onValueChange = { password = it }, label = "Senha", placeholder = "••••••••",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(text = "Cadastrar", onClick = onRegisterClick)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Já tem uma conta? ")
            TextButton(onClick = onBackToLoginClick) { Text("Entrar", color = PrimaryBlue, fontWeight = FontWeight.Bold) }
        }
    }
}
