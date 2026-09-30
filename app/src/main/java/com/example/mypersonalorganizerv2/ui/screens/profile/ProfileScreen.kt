package com.example.mypersonalorganizerv2.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mypersonalorganizerv2.navigation.Screen
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onSaveClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var emailNotifications by remember { mutableStateOf(true) }
    var smsNotifications by remember { mutableStateOf(true) }

    MainScaffold(
        title = "Meu perfil",
        currentRoute = Screen.Profile.route,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).size(32.dp).clip(CircleShape).background(PrimaryBlue), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            CustomTextField(value = name, onValueChange = { name = it }, label = "Nome completo", placeholder = "digite seu nome", leadingIcon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = email, onValueChange = { email = it }, label = "E-mail", placeholder = "digite seu e-mail", leadingIcon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = phone, onValueChange = { phone = it }, label = "Telefone", placeholder = "(00) 00000-0000", leadingIcon = Icons.Default.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = address, onValueChange = { address = it }, label = "Endereço", placeholder = "rua, número, bairro", leadingIcon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Preferências de notificação", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Lembretes por e-mail", modifier = Modifier.weight(1f))
                        Switch(checked = emailNotifications, onCheckedChange = { emailNotifications = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Lembretes por SMS", modifier = Modifier.weight(1f))
                        Switch(checked = smsNotifications, onCheckedChange = { smsNotifications = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Salvar alterações", onClick = onSaveClick)
        }
    }
}
