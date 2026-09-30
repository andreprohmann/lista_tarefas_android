package com.example.mypersonalorganizerv2.ui.screens.appointment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypersonalorganizerv2.navigation.Screen
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

@Composable
fun AppointmentScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onConfirmClick: () -> Unit
) {
    var serviceType by remember { mutableStateOf("Consulta") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    MainScaffold(
        title = "Agendar compromisso",
        currentRoute = Screen.Appointment.route,
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
            Text("Agendar compromisso", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Tipo de serviço", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(serviceType, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PrimaryBlue)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = date, onValueChange = { date = it }, label = "Data", placeholder = "dd/mm/aaaa", leadingIcon = Icons.Default.CalendarToday)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = time, onValueChange = { time = it }, label = "Horário", placeholder = "hh:mm", leadingIcon = Icons.Default.AccessTime)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = location, onValueChange = { location = it }, label = "Local", placeholder = "endereço ou local", leadingIcon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Observações", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            OutlinedTextField(
                value = observations, onValueChange = { observations = it }, modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("anotações adicionais") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.StickyNote2, contentDescription = null, tint = PrimaryBlue) },
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Confirmar agendamento", onClick = onConfirmClick)
        }
    }
}
