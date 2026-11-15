package com.example.mypersonalorganizerv2.ui.screens.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.navigation.TaskList
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.components.PrimaryButton
import com.example.mypersonalorganizerv2.ui.components.PriorityOption
import com.example.mypersonalorganizerv2.ui.components.SelectField
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.TaskViewModel

// TELA DE FORMULÁRIO DE TAREFA (CRUD 1: Criar e Alterar).
// A MESMA tela serve para criar e para editar:
// taskId = -1 -> nova tarefa | taskId = número real -> editar essa tarefa
@Composable
fun TaskFormScreen(
    taskId: Long,                   // id recebido pela navegação
    onNavigate: (Any) -> Unit,
    onLogout: () -> Unit,
    onSaved: () -> Unit,            // chamado depois de salvar (volta para a lista)
    viewModel: TaskViewModel = viewModel()
) {
    // LaunchedEffect: ao abrir a tela, prepara o formulário.
    // Se for edição, busca a tarefa no banco e preenche os campos.
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    MainScaffold(
        // O título muda conforme o modo: editando ou criando
        title = if (viewModel.editingTask != null) "Editar tarefa" else "Nova tarefa",
        currentRoute = TaskList,
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
            CustomTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it; viewModel.clearError() },
                label = "Título da tarefa",
                placeholder = "o que precisa ser feito?",
                isError = viewModel.errorMessage != null
            )
            Spacer(modifier = Modifier.height(16.dp))
            SelectField(
                label = "Categoria",
                selected = viewModel.category.ifBlank { "Sem categoria (cadastre no menu)" },
                options = viewModel.categories, // categorias cadastradas pelo usuário
                onSelect = { viewModel.category = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Prioridade", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            // Escolha da prioridade (só uma pode ficar marcada)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PriorityOption("Alta", viewModel.priority == "Alta") { viewModel.priority = "Alta" }
                PriorityOption("Média", viewModel.priority == "Média") { viewModel.priority = "Média" }
                PriorityOption("Baixa", viewModel.priority == "Baixa") { viewModel.priority = "Baixa" }
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(value = viewModel.deliveryDate, onValueChange = { viewModel.deliveryDate = it }, label = "Data de entrega", placeholder = "dd/mm/aaaa", leadingIcon = Icons.Default.CalendarToday)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Anotações", fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
            OutlinedTextField(value = viewModel.notes, onValueChange = { viewModel.notes = it }, modifier = Modifier.fillMaxWidth().height(120.dp), placeholder = { Text("detalhes adicionais") }, shape = RoundedCornerShape(12.dp))

            // Mostra o erro (ex.: título vazio)
            viewModel.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Botão SALVAR: o ViewModel cria (insert) ou altera (update) no banco.
            // Se salvou, volta para a lista.
            PrimaryButton(text = "Salvar tarefa", onClick = {
                if (viewModel.saveTask()) onSaved()
            })
        }
    }
}
