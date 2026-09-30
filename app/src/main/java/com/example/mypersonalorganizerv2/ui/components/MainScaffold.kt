package com.example.mypersonalorganizerv2.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.mypersonalorganizerv2.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    title: String,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    showMenu: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {  },
                actions = {
                    if (showMenu) {
                        IconButton(onClick = { onNavigate(Screen.Appointment.route) }) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Agendamentos",
                                tint = if (currentRoute == Screen.Appointment.route) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { onNavigate(Screen.NewTask.route) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Nova Tarefa",
                                tint = if (currentRoute == Screen.NewTask.route) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { onNavigate(Screen.Profile.route) }) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = if (currentRoute == Screen.Profile.route) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sair"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
