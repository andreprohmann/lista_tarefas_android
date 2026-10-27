package com.example.mypersonalorganizerv2.ui.screens.task

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.model.Task
import com.example.mypersonalorganizerv2.navigation.Screen
import com.example.mypersonalorganizerv2.ui.components.DeleteConfirmDialog
import com.example.mypersonalorganizerv2.ui.components.EmptyState
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.TaskViewModel

// TELA DE LISTA DE TAREFAS (CRUD 1: Listar, Alterar "concluída" e Deletar).
// Usa o MainScaffold, então TEM menu.
@Composable
fun TaskListScreen(
    onNavigate: (String) -> Unit,   // navegação do menu
    onLogout: () -> Unit,           // botão Sair
    onAddTask: () -> Unit,          // botão "+" (nova tarefa)
    onEditTask: (Long) -> Unit,     // abrir uma tarefa para editar
    viewModel: TaskViewModel = viewModel()
) {
    // Guarda qual tarefa o usuário quer apagar (null = janela de confirmação fechada)
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    // LaunchedEffect: sempre que a tela abre, busca as tarefas no banco.
    // Por isso, ao voltar do formulário, a lista já aparece atualizada.
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    MainScaffold(
        title = "Minhas tarefas",
        currentRoute = Screen.TaskList.route,
        onNavigate = onNavigate,
        onLogout = onLogout,
        // Botão redondo "+" no canto da tela: cria uma nova tarefa
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask, containerColor = PrimaryBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Nova tarefa")
            }
        }
    ) { innerPadding ->
        if (viewModel.tasks.isEmpty()) {
            // Sem tarefas: mostra uma mensagem
            EmptyState(
                message = "Nenhuma tarefa cadastrada.\nToque em + para criar a primeira.",
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
                // Para cada tarefa da lista, desenha um cartão (TaskItem).
                // key = id ajuda o Compose a saber qual item mudou.
                items(viewModel.tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggleDone = { viewModel.toggleDone(task) },
                        onEdit = { onEditTask(task.id) },
                        onDelete = { taskToDelete = task }
                    )
                }
            }
        }
    }

    // Se o usuário tocou na lixeira, abre a confirmação.
    // Confirmou -> DELETAR no banco. Cancelou -> só fecha a janela.
    taskToDelete?.let { task ->
        DeleteConfirmDialog(
            itemName = task.title,
            onConfirm = {
                viewModel.deleteTask(task)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}

// CARTÃO de uma tarefa na lista.
// Tem: checkbox (concluída), título, detalhes, botão editar e botão excluir.
@Composable
private fun TaskItem(
    task: Task,
    onToggleDone: () -> Unit,
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
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Checkbox: marca/desmarca como concluída (ALTERAR no banco)
            Checkbox(
                checked = task.done,
                onCheckedChange = { onToggleDone() },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
            )
            Column(modifier = Modifier.weight(1f)) {
                // Título riscado quando a tarefa está concluída
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                )
                // Detalhes juntos numa linha, ex.: "Estudos • Prioridade Alta • 10/11"
                Text(
                    text = listOf(task.category, "Prioridade ${task.priority}", task.deliveryDate)
                        .filter { it.isNotBlank() }
                        .joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
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
