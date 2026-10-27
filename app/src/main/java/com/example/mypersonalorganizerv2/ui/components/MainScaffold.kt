package com.example.mypersonalorganizerv2.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.mypersonalorganizerv2.navigation.Screen

// ESTRUTURA PADRÃO das telas internas: barra de cima com o título e o MENU.
// Só as telas internas (tarefas, compromissos, perfil) usam este componente.
// Login e Cadastro NÃO usam, por isso não mostram o menu (requisito do trabalho).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    title: String,                                       // título que aparece no topo
    currentRoute: String?,                               // tela atual (para pintar o ícone ativo de azul)
    onNavigate: (String) -> Unit,                        // o que fazer ao tocar em um item do menu
    onLogout: () -> Unit,                                // o que fazer ao tocar em "Sair"
    floatingActionButton: @Composable () -> Unit = {},   // botão redondo "+" (opcional)
    content: @Composable (PaddingValues) -> Unit         // o conteúdo da tela
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                // Ícones do MENU, no canto direito da barra
                actions = {
                    // Menu: Tarefas
                    IconButton(onClick = { onNavigate(Screen.TaskList.route) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Tarefas",
                            tint = if (currentRoute == Screen.TaskList.route) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    // Menu: Compromissos
                    IconButton(onClick = { onNavigate(Screen.AppointmentList.route) }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Compromissos",
                            tint = if (currentRoute == Screen.AppointmentList.route) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    // Menu: Perfil
                    IconButton(onClick = { onNavigate(Screen.Profile.route) }) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = if (currentRoute == Screen.Profile.route) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    // Menu: Sair (encerra a sessão)
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        },
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        content(innerPadding)
    }
}
