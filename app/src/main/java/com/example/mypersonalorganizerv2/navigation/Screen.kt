package com.example.mypersonalorganizerv2.navigation

// Lista de TODAS as telas (rotas) do app.
// Cada "route" é como um endereço usado para navegar até a tela.
sealed class Screen(val route: String) {
    object Login : Screen("login")             // tela de entrar
    object Register : Screen("register")       // tela de criar conta
    object TaskList : Screen("task_list")      // lista de tarefas

    // Formulário de tarefa. O {taskId} é um valor que vai junto na rota:
    // -1 = nova tarefa | outro número = editar a tarefa com esse id
    object TaskForm : Screen("task_form/{taskId}") {
        fun createRoute(taskId: Long) = "task_form/$taskId"
    }

    object AppointmentList : Screen("appointment_list") // lista de compromissos

    // Formulário de compromisso: -1 = novo | outro número = editar
    object AppointmentForm : Screen("appointment_form/{appointmentId}") {
        fun createRoute(appointmentId: Long) = "appointment_form/$appointmentId"
    }

    object Profile : Screen("profile") // tela de perfil
}
