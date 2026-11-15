package com.example.mypersonalorganizerv2.ui.screens.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.model.Contact
import com.example.mypersonalorganizerv2.navigation.ContactList
import com.example.mypersonalorganizerv2.ui.components.DeleteConfirmDialog
import com.example.mypersonalorganizerv2.ui.components.EmptyState
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PhotoPicker
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.ContactViewModel

// TELA DE LISTA DE CONTATOS (CRUD 4: Listar e Deletar).
// Funciona igual à lista de tarefas. Usa o MainScaffold, então TEM menu.
@Composable
fun ContactListScreen(
    onNavigate: (Any) -> Unit,
    onLogout: () -> Unit,
    onAddContact: () -> Unit,          // botão "+" (novo contato)
    onEditContact: (Long) -> Unit,     // abrir um contato para editar
    viewModel: ContactViewModel = viewModel()
) {
    // Guarda qual contato o usuário quer apagar (null = confirmação fechada)
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    // LaunchedEffect: ao abrir a tela (e ao voltar do formulário), busca os contatos no banco
    LaunchedEffect(viewModel.userId) {
        viewModel.loadContacts()
    }

    MainScaffold(
        title = "Contatos",
        currentRoute = ContactList,
        onNavigate = onNavigate,
        onLogout = onLogout,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContact, containerColor = PrimaryBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Novo contato")
            }
        }
    ) { innerPadding ->
        if (viewModel.contacts.isEmpty()) {
            EmptyState(
                message = "Nenhum contato cadastrado.\nToque em + para adicionar.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.contacts, key = { it.id }) { contact ->
                    ContactItem(
                        contact = contact,
                        onEdit = { onEditContact(contact.id) },
                        onDelete = { contactToDelete = contact }
                    )
                }
            }
        }
    }

    // Confirmação antes de DELETAR
    contactToDelete?.let { contact ->
        DeleteConfirmDialog(
            itemName = contact.name,
            onConfirm = {
                viewModel.deleteContact(contact)
                contactToDelete = null
            },
            onDismiss = { contactToDelete = null }
        )
    }
}

// CARTÃO de um contato: foto, nome, telefone/e-mail, editar e excluir.
@Composable
private fun ContactItem(
    contact: Contact,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Foto pequena (só mostra; não abre a galeria)
            PhotoPicker(photoPath = contact.photo, size = 48.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, fontWeight = FontWeight.Bold)
                Text(
                    text = listOf(contact.phone, contact.email).filter { it.isNotBlank() }.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PrimaryBlue)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
