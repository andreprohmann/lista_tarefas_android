package com.example.mypersonalorganizerv2.ui.screens.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.navigation.ContactList
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PhotoPicker
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.ContactViewModel

// TELA DE FORMULÁRIO DE CONTATO (CRUD 4: Criar e Alterar).
// contactId = -1 -> novo | número real -> editar esse contato.
// Usa a GALERIA (foto) e a API ViaCEP (endereço pelo CEP).
@Composable
fun ContactFormScreen(
    contactId: Long,                // id recebido pela navegação
    onNavigate: (Any) -> Unit,
    onLogout: () -> Unit,
    onSaved: () -> Unit,            // chamado depois de salvar (volta para a lista)
    viewModel: ContactViewModel = viewModel()
) {
    // LaunchedEffect: ao abrir a tela, se for edição, busca o contato no banco
    LaunchedEffect(contactId) {
        viewModel.loadContact(contactId)
    }

    MainScaffold(
        title = if (viewModel.editingContact != null) "Editar contato" else "Novo contato",
        currentRoute = ContactList,
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
            // Foto: ao tocar, abre a GALERIA do celular
            PhotoPicker(photoPath = viewModel.photo, onImagePicked = viewModel::onPhotoPicked)
            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(value = viewModel.name, onValueChange = { viewModel.name = it; viewModel.clearError() }, label = "Nome", placeholder = "nome do contato", leadingIcon = Icons.Default.Person, isError = viewModel.errorMessage != null)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.phone, onValueChange = { viewModel.phone = it }, label = "Telefone", placeholder = "(00) 00000-0000", leadingIcon = Icons.Default.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.email, onValueChange = { viewModel.email = it; viewModel.clearError() }, label = "E-mail", placeholder = "email@exemplo.com", leadingIcon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))

            // CEP + botão de busca na API (o botão fica dentro do campo, à direita)
            CustomTextField(
                value = viewModel.cep,
                onValueChange = { viewModel.cep = it; viewModel.clearError() },
                label = "CEP",
                placeholder = "00000-000",
                leadingIcon = Icons.Default.LocationOn,
                trailingIcon = {
                    if (viewModel.searchingCep) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = viewModel::searchCep) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar endereço pelo CEP", tint = PrimaryBlue)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.address, onValueChange = { viewModel.address = it }, label = "Endereço", placeholder = "preenchido pelo CEP ou digitado", leadingIcon = Icons.Default.LocationOn)

            // Mostra o erro (ex.: nome vazio, CEP não encontrado)
            viewModel.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Botão SALVAR: o ViewModel cria (insert) ou altera (update) no banco
            PrimaryButton(
                text = if (viewModel.editingContact != null) "Salvar alterações" else "Salvar contato",
                onClick = { if (viewModel.saveContact()) onSaved() }
            )
        }
    }
}
