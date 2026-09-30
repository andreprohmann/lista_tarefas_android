package com.example.mypersonalorganizerv2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypersonalorganizerv2.ui.screens.appointment.AppointmentScreen
import com.example.mypersonalorganizerv2.ui.screens.login.LoginScreen
import com.example.mypersonalorganizerv2.ui.screens.newtask.NewTaskScreen
import com.example.mypersonalorganizerv2.ui.screens.profile.ProfileScreen
import com.example.mypersonalorganizerv2.ui.screens.register.RegisterScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Appointment.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { },
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Appointment.route) {
            AppointmentScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onConfirmClick = {
                    navController.navigate(Screen.NewTask.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.NewTask.route) {
            NewTaskScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSaveTaskClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
