package com.example.mypersonalorganizerv2.navigation

import kotlinx.serialization.Serializable

// ROTAS SERIALIZADAS: cada tela do app é um objeto (ou classe) marcado com @Serializable.
// "data object" = tela sem parâmetros | "data class" = tela que recebe valores.
// Para navegar: navController.navigate(TaskForm(taskId = 5))
// Para ler o valor na tela de destino: backStackEntry.toRoute<TaskForm>().taskId

@Serializable
data object Login            // tela de entrar

@Serializable
data object Register         // tela de criar conta

@Serializable
data object TaskList         // lista de tarefas

// Formulário de tarefa: -1 = nova tarefa | outro número = editar a tarefa com esse id
@Serializable
data class TaskForm(val taskId: Long = -1)

@Serializable
data object AppointmentList  // lista de compromissos

// Formulário de compromisso: -1 = novo | outro número = editar
@Serializable
data class AppointmentForm(val appointmentId: Long = -1)

@Serializable
data object CategoryList     // lista de categorias (criar/editar é feito numa janela na própria tela)

@Serializable
data object ContactList      // lista de contatos

// Formulário de contato: -1 = novo | outro número = editar
@Serializable
data class ContactForm(val contactId: Long = -1)

@Serializable
data object Profile          // tela de perfil
