package com.example.mypersonalorganizerv2.ui.screens.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.model.Appointment
import com.example.mypersonalorganizerv2.navigation.AppointmentList
import com.example.mypersonalorganizerv2.ui.components.DeleteConfirmDialog
import com.example.mypersonalorganizerv2.ui.components.EmptyState
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.AppointmentViewModel

// TELA DE LISTA DE COMPROMISSOS (CRUD 2: Listar e Deletar).
// Funciona igual à lista de tarefas. Usa o MainScaffold, então TEM menu.
@Composable
fun AppointmentListScreen(
    onNavigate: (Any) -> Unit,          // navegação do menu
    onLogout: () -> Unit,                  // botão Sair
    onAddAppointment: () -> Unit,          // botão "+" (novo compromisso)
    onEditAppointment: (Long) -> Unit,     // abrir um compromisso para editar
    viewModel: AppointmentViewModel = viewModel()
) {
    // Guarda qual compromisso o usuário quer apagar (null = confirmação fechada)
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }

    // LaunchedEffect: sempre que a tela abre, busca os compromissos no banco
    LaunchedEffect(viewModel.userId) {
        viewModel.loadAppointments()
    }

    MainScaffold(
        title = "Compromissos",
        currentRoute = AppointmentList,
        onNavigate = onNavigate,
        onLogout = onLogout,
        // Botão redondo "+": cria um novo compromisso
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAppointment, containerColor = PrimaryBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Novo compromisso")
            }
        }
    ) { innerPadding ->
        if (viewModel.appointments.isEmpty()) {
            // Sem compromissos: mostra uma mensagem
            EmptyState(
                message = "Nenhum compromisso agendado.\nToque em + para agendar.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            // LazyColumn: lista com rolagem que só desenha os itens visíveis na tela
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Um cartão (AppointmentItem) para cada compromisso
                items(viewModel.appointments, key = { it.id }) { appointment ->
                    AppointmentItem(
                        appointment = appointment,
                        onEdit = { onEditAppointment(appointment.id) },
                        onDelete = { appointmentToDelete = appointment }
                    )
                }
            }
        }
    }

    // Confirmação antes de DELETAR do banco
    appointmentToDelete?.let { appointment ->
        DeleteConfirmDialog(
            itemName = "${appointment.serviceType} em ${appointment.date}",
            onConfirm = {
                viewModel.deleteAppointment(appointment)
                appointmentToDelete = null
            },
            onDismiss = { appointmentToDelete = null }
        )
    }
}

// CARTÃO de um compromisso na lista.
// Mostra tipo, data/horário, local e os botões editar e excluir.
@Composable
private fun AppointmentItem(
    appointment: Appointment,
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
            Icon(Icons.Default.Event, contentDescription = null, tint = PrimaryBlue)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(appointment.serviceType, fontWeight = FontWeight.Bold)
                Text(
                    text = "${appointment.date} às ${appointment.time}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (appointment.location.isNotBlank()) {
                    Text(appointment.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            // Lápis: abre o formulário para editar
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PrimaryBlue)
            }
            // Lixeira: pede confirmação para excluir
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
