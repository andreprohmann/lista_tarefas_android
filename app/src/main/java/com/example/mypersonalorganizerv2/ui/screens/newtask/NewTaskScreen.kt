package com.example.mypersonalorganizerv2.ui.screens.newtask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mypersonalorganizerv2.navigation.Screen
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.components.PriorityOption
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

@Composable
fun NewTaskScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onSaveTaskClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pessoal") }
    var priority by remember { mutableStateOf("Média") }
    var deliveryDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    MainScaffold(
        title = "Nova tarefa",
        currentRoute = Screen.NewTask.route,
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
            CustomTextField(value = title, onValueChange = { title = it }, label = "Título da tarefa", placeholder = "o que precisa ser feito?")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Categoria", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(category, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PrimaryBlue)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Prioridade", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PriorityOption("Alta", priority == "Alta") { priority = "Alta" }
                PriorityOption("Média", priority == "Média") { priority = "Média" }
                PriorityOption("Baixa", priority == "Baixa") { priority = "Baixa" }
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = deliveryDate, onValueChange = { deliveryDate = it }, label = "Data de entrega", placeholder = "dd/mm/aaaa", leadingIcon = Icons.Default.CalendarToday)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Anotações", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth().height(120.dp), placeholder = { Text("detalhes adicionais") }, shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Salvar tarefa", onClick = onSaveTaskClick)
        }
    }
}
