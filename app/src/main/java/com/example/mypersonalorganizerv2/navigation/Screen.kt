package com.example.mypersonalorganizerv2.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Appointment : Screen("appointment")
    object Profile : Screen("profile")
    object NewTask : Screen("new_task")
}
