package com.example.mypersonalorganizerv2.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mypersonalorganizerv2.ui.screens.appointment.AppointmentFormScreen
import com.example.mypersonalorganizerv2.ui.screens.appointment.AppointmentListScreen
import com.example.mypersonalorganizerv2.ui.screens.login.LoginScreen
import com.example.mypersonalorganizerv2.ui.screens.profile.ProfileScreen
import com.example.mypersonalorganizerv2.ui.screens.register.RegisterScreen
import com.example.mypersonalorganizerv2.ui.screens.task.TaskFormScreen
import com.example.mypersonalorganizerv2.ui.screens.task.TaskListScreen
import com.example.mypersonalorganizerv2.viewmodel.AuthViewModel

// "MAPA" do app: diz qual tela abre em cada rota e como ir de uma tela para outra.
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController() // controla a navegação
) {
    // ViewModel de autenticação usado aqui só para fazer o logout
    val authViewModel: AuthViewModel = viewModel()

    // Usado pelos botões do MENU para trocar de tela.
    // launchSingleTop = não abre a mesma tela duas vezes seguidas.
    val onNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Screen.TaskList.route)
            launchSingleTop = true
        }
    }

    // Usado pelo botão SAIR do menu.
    val onLogout: () -> Unit = {
        authViewModel.logout() // 1) encerra a sessão
        // 2) vai para o login e limpa todas as telas anteriores,
        //    assim o botão "voltar" do celular não volta para dentro do app
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // o app sempre começa pela tela de login
    ) {
        // Tela de LOGIN (sem menu)
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Ao entrar, vai para as tarefas e tira o login da pilha
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { },
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Tela de CADASTRO (sem menu)
        composable(Screen.Register.route) {
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
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onNavigate = onNavigate,
                onLogout = onLogout,
                onAddTask = { navController.navigate(Screen.TaskForm.createRoute(-1)) },               // -1 = nova
                onEditTask = { taskId -> navController.navigate(Screen.TaskForm.createRoute(taskId)) } // id = editar
            )
        }

        // FORMULÁRIO de tarefa (recebe o taskId pela rota)
        composable(
            route = Screen.TaskForm.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            TaskFormScreen(
                taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L, // lê o id que veio na rota
                onNavigate = onNavigate,
                onLogout = onLogout,
                onSaved = { navController.popBackStack() } // salvou: volta para a lista
            )
        }

        // LISTA de compromissos
        composable(Screen.AppointmentList.route) {
            AppointmentListScreen(
                onNavigate = onNavigate,
                onLogout = onLogout,
                onAddAppointment = { navController.navigate(Screen.AppointmentForm.createRoute(-1)) },        // -1 = novo
                onEditAppointment = { id -> navController.navigate(Screen.AppointmentForm.createRoute(id)) } // id = editar
            )
        }

        // FORMULÁRIO de compromisso (recebe o appointmentId pela rota)
        composable(
            route = Screen.AppointmentForm.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.LongType })
        ) { backStackEntry ->
            AppointmentFormScreen(
                appointmentId = backStackEntry.arguments?.getLong("appointmentId") ?: -1L,
                onNavigate = onNavigate,
                onLogout = onLogout,
                onSaved = { navController.popBackStack() }
            )
        }

        // PERFIL
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigate = onNavigate,
                onLogout = onLogout
            )
        }
    }
}
