package com.example.mypersonalorganizerv2.ui.screens.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.navigation.Screen
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.components.SelectField
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.AppointmentViewModel

// Opções do campo "Tipo de serviço"
private val serviceTypes = listOf("Consulta", "Exame", "Reunião", "Aula", "Outro")

// TELA DE FORMULÁRIO DE COMPROMISSO (CRUD 2: Criar e Alterar).
// A MESMA tela serve para criar e editar:
// appointmentId = -1 -> novo | número real -> editar esse compromisso
@Composable
fun AppointmentFormScreen(
    appointmentId: Long,            // id recebido pela navegação
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onSaved: () -> Unit,            // chamado depois de salvar (volta para a lista)
    viewModel: AppointmentViewModel = viewModel()
) {
    // LaunchedEffect: ao abrir a tela, se for edição, busca o compromisso no banco
    LaunchedEffect(appointmentId) {
        viewModel.loadAppointment(appointmentId)
    }

    MainScaffold(
        title = if (viewModel.editingAppointment != null) "Editar compromisso" else "Agendar compromisso",
        currentRoute = Screen.AppointmentList.route,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Todos os campos leem e gravam o valor direto no ViewModel
            SelectField(
                label = "Tipo de serviço",
                selected = viewModel.serviceType,
                options = serviceTypes,
                onSelect = { viewModel.serviceType = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.date, onValueChange = { viewModel.date = it; viewModel.clearError() }, label = "Data", placeholder = "dd/mm/aaaa", leadingIcon = Icons.Default.CalendarToday, isError = viewModel.errorMessage != null)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.time, onValueChange = { viewModel.time = it; viewModel.clearError() }, label = "Horário", placeholder = "hh:mm", leadingIcon = Icons.Default.AccessTime, isError = viewModel.errorMessage != null)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.location, onValueChange = { viewModel.location = it }, label = "Local", placeholder = "endereço ou local", leadingIcon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Observações", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            OutlinedTextField(
                value = viewModel.observations, onValueChange = { viewModel.observations = it }, modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("anotações adicionais") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.StickyNote2, contentDescription = null, tint = PrimaryBlue) },
                shape = RoundedCornerShape(12.dp)
            )

            // Mostra o erro (ex.: data ou horário vazios)
            viewModel.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Botão SALVAR: o ViewModel cria (insert) ou altera (update) no banco.
            // Se salvou, volta para a lista.
            PrimaryButton(
                text = if (viewModel.editingAppointment != null) "Salvar alterações" else "Confirmar agendamento",
                onClick = { if (viewModel.saveAppointment()) onSaved() }
            )
        }
    }
}
