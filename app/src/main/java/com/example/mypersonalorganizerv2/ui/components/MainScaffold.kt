package com.example.mypersonalorganizerv2.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.mypersonalorganizerv2.navigation.AppointmentList
import com.example.mypersonalorganizerv2.navigation.CategoryList
import com.example.mypersonalorganizerv2.navigation.ContactList
import com.example.mypersonalorganizerv2.navigation.Profile
import com.example.mypersonalorganizerv2.navigation.TaskList

// Um item do menu: texto, ícone e a rota (objeto @Serializable) para onde ele leva.
private data class MenuItem(val label: String, val icon: ImageVector, val route: Any)

private val menuItems = listOf(
    MenuItem("Tarefas", Icons.AutoMirrored.Filled.List, TaskList),
    MenuItem("Compromissos", Icons.Default.CalendarToday, AppointmentList),
    MenuItem("Contatos", Icons.Default.Contacts, ContactList),
    MenuItem("Categorias", Icons.AutoMirrored.Filled.Label, CategoryList),
    MenuItem("Perfil", Icons.Default.Person, Profile)
)

// ESTRUTURA PADRÃO das telas internas: barra de cima com o título e o MENU.
// Só as telas internas usam este componente.
// Login e Cadastro NÃO usam, por isso não mostram o menu (requisito do trabalho).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    title: String,                                       // título que aparece no topo
    currentRoute: Any,                                   // tela atual (para destacar o item no menu)
    onNavigate: (Any) -> Unit,                           // o que fazer ao tocar em um item do menu
    onLogout: () -> Unit,                                // o que fazer ao tocar em "Sair"
    floatingActionButton: @Composable () -> Unit = {},   // botão redondo "+" (opcional)
    content: @Composable (PaddingValues) -> Unit         // o conteúdo da tela
) {
    // Guarda se o menu está aberto (true) ou fechado (false)
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                actions = {
                    // Botão do MENU: ao tocar, abre a lista de telas
                    Box {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            menuItems.forEach { item ->
                                // A tela atual aparece destacada em azul
                                val color = if (item.route == currentRoute) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                DropdownMenuItem(
                                    text = { Text(item.label, color = color) },
                                    leadingIcon = { Icon(item.icon, contentDescription = null, tint = color) },
                                    onClick = {
                                        menuOpen = false
                                        onNavigate(item.route)
                                    }
                                )
                            }
                            HorizontalDivider()
                            // Sair: encerra a sessão
                            DropdownMenuItem(
                                text = { Text("Sair") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        content(innerPadding)
    }
}
