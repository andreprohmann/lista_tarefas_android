package com.example.mypersonalorganizerv2.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.navigation.Profile
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PhotoPicker
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.ProfileViewModel

// TELA DE PERFIL: mostra e altera os dados do usuário logado.
// Usa o MainScaffold, então TEM menu.
@Composable
fun ProfileScreen(
    onNavigate: (Any) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // Preferências de notificação (só visuais, não são salvas no banco)
    var emailNotifications by remember { mutableStateOf(true) }
    var smsNotifications by remember { mutableStateOf(true) }

    // LaunchedEffect: ao abrir a tela, busca no banco os dados do usuário logado
    LaunchedEffect(viewModel.userId) {
        viewModel.loadUser()
    }

    MainScaffold(
        title = "Meu perfil",
        currentRoute = Profile,
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
            // Foto do perfil: ao tocar, abre a GALERIA do celular (recurso nativo)
            PhotoPicker(photoPath = viewModel.photo, onImagePicked = viewModel::onPhotoPicked)
            Spacer(modifier = Modifier.height(32.dp))
            CustomTextField(value = viewModel.name, onValueChange = { viewModel.name = it; viewModel.clearMessage() }, label = "Nome completo", placeholder = "digite seu nome", leadingIcon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.email, onValueChange = { viewModel.email = it; viewModel.clearMessage() }, label = "E-mail", placeholder = "digite seu e-mail", leadingIcon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.phone, onValueChange = { viewModel.phone = it; viewModel.clearMessage() }, label = "Telefone", placeholder = "(00) 00000-0000", leadingIcon = Icons.Default.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.address, onValueChange = { viewModel.address = it; viewModel.clearMessage() }, label = "Endereço", placeholder = "rua, número, bairro", leadingIcon = Icons.Default.LocationOn)
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
            // Mensagem após salvar: azul = sucesso, vermelho = erro
            viewModel.message?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = if (viewModel.isError) MaterialTheme.colorScheme.error else PrimaryBlue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Botão SALVAR: o ViewModel valida e atualiza o usuário no banco
            PrimaryButton(text = "Salvar alterações", onClick = { viewModel.saveUser() })
        }
    }
}
