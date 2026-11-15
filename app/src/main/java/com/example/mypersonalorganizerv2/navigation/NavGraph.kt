package com.example.mypersonalorganizerv2.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mypersonalorganizerv2.ui.screens.appointment.AppointmentFormScreen
import com.example.mypersonalorganizerv2.ui.screens.appointment.AppointmentListScreen
import com.example.mypersonalorganizerv2.ui.screens.category.CategoryListScreen
import com.example.mypersonalorganizerv2.ui.screens.contact.ContactFormScreen
import com.example.mypersonalorganizerv2.ui.screens.contact.ContactListScreen
import com.example.mypersonalorganizerv2.ui.screens.login.LoginScreen
import com.example.mypersonalorganizerv2.ui.screens.profile.ProfileScreen
import com.example.mypersonalorganizerv2.ui.screens.register.RegisterScreen
import com.example.mypersonalorganizerv2.ui.screens.task.TaskFormScreen
import com.example.mypersonalorganizerv2.ui.screens.task.TaskListScreen
import com.example.mypersonalorganizerv2.viewmodel.AuthViewModel

// "MAPA" do app: diz qual tela abre em cada rota e como ir de uma tela para outra.
// As rotas são os objetos @Serializable do arquivo Routes.kt.
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController() // controla a navegação
) {
    // ViewModel de autenticação usado aqui só para fazer o logout
    val authViewModel: AuthViewModel = viewModel()

    // Usado pelos itens do MENU para trocar de tela.
    // popUpTo(TaskList) = as telas do menu não se acumulam na pilha.
    // launchSingleTop = não abre a mesma tela duas vezes seguidas.
    val onNavigate: (Any) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(TaskList)
            launchSingleTop = true
        }
    }

    // Usado pelo item SAIR do menu.
    val onLogout: () -> Unit = {
        authViewModel.logout() // 1) encerra a sessão
        // 2) vai para o login e limpa todas as telas anteriores,
        //    assim o botão "voltar" do celular não volta para dentro do app
        navController.navigate(Login) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Login // o app sempre começa pela tela de login
    ) {
        // Tela de LOGIN (sem menu)
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    // Ao entrar, vai para as tarefas e tira o login da pilha
                    navController.navigate(TaskList) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { },
                onCreateAccountClick = {
                    navController.navigate(Register)
                }
            )
        }

        // Tela de CADASTRO (sem menu)
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack() // conta criada: volta para o login
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // LISTA de tarefas
        composable<TaskList> {
            TaskListScreen(
                onNavigate = onNavigate,
                onLogout = onLogout,
                onAddTask = { navController.navigate(TaskForm()) },                    // sem id = nova
                onEditTask = { taskId -> navController.navigate(TaskForm(taskId)) }   // id = editar
            )
        }

        // FORMULÁRIO de tarefa: toRoute lê o objeto TaskForm que veio na navegação
        composable<TaskForm> { backStackEntry ->
            val route = backStackEntry.toRoute<TaskForm>()
            TaskFormScreen(
                taskId = route.taskId,
                onNavigate = onNavigate,
                onLogout = onLogout,
                onSaved = { navController.popBackStack() } // salvou: volta para a lista
            )
        }

        // LISTA de compromissos
        composable<AppointmentList> {
            AppointmentListScreen(
                onNavigate = onNavigate,
                onLogout = onLogout,
                onAddAppointment = { navController.navigate(AppointmentForm()) },
                onEditAppointment = { id -> navController.navigate(AppointmentForm(id)) }
            )
        }

        // FORMULÁRIO de compromisso
        composable<AppointmentForm> { backStackEntry ->
            val route = backStackEntry.toRoute<AppointmentForm>()
            AppointmentFormScreen(
                appointmentId = route.appointmentId,
                onNavigate = onNavigate,
                onLogout = onLogout,
                onSaved = { navController.popBackStack() }
            )
        }

        // LISTA de categorias (criar e editar acontecem numa janela na própria tela)
        composable<CategoryList> {
            CategoryListScreen(
                onNavigate = onNavigate,
                onLogout = onLogout
            )
        }

        // LISTA de contatos
        composable<ContactList> {
            ContactListScreen(
                onNavigate = onNavigate,
                onLogout = onLogout,
                onAddContact = { navController.navigate(ContactForm()) },
                onEditContact = { id -> navController.navigate(ContactForm(id)) }
            )
        }

        // FORMULÁRIO de contato
        composable<ContactForm> { backStackEntry ->
            val route = backStackEntry.toRoute<ContactForm>()
            ContactFormScreen(
                contactId = route.contactId,
                onNavigate = onNavigate,
                onLogout = onLogout,
                onSaved = { navController.popBackStack() }
            )
        }

        // PERFIL
        composable<Profile> {
            ProfileScreen(
                onNavigate = onNavigate,
                onLogout = onLogout
            )
        }
    }
}
