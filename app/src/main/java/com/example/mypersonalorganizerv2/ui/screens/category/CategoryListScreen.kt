package com.example.mypersonalorganizerv2.ui.screens.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypersonalorganizerv2.model.Category
import com.example.mypersonalorganizerv2.navigation.CategoryList
import com.example.mypersonalorganizerv2.ui.components.CustomTextField
import com.example.mypersonalorganizerv2.ui.components.DeleteConfirmDialog
import com.example.mypersonalorganizerv2.ui.components.EmptyState
import com.example.mypersonalorganizerv2.ui.components.MainScaffold
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue
import com.example.mypersonalorganizerv2.viewmodel.CategoryViewModel

// TELA DE CATEGORIAS (CRUD 3 completo nesta tela):
// Listar = LazyColumn | Criar/Alterar = janela com o campo nome | Deletar = lixeira.
@Composable
fun CategoryListScreen(
    onNavigate: (Any) -> Unit,
    onLogout: () -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    // Guarda qual categoria o usuário quer apagar (null = confirmação fechada)
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // LaunchedEffect: ao abrir a tela, busca as categorias do usuário logado
    LaunchedEffect(viewModel.userId) {
        viewModel.loadCategories()
    }

    MainScaffold(
        title = "Categorias",
        currentRoute = CategoryList,
        onNavigate = onNavigate,
        onLogout = onLogout,
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openNew, containerColor = PrimaryBlue, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Nova categoria")
            }
        }
    ) { innerPadding ->
        if (viewModel.categories.isEmpty()) {
            EmptyState(
                message = "Nenhuma categoria cadastrada.\nToque em + para criar.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.categories, key = { it.id }) { category ->
                    Card(
                        onClick = { viewModel.openEdit(category) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(category.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.openEdit(category) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PrimaryBlue)
                            }
                            IconButton(onClick = { categoryToDelete = category }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    // Janela de CRIAR / ALTERAR
    if (viewModel.dialogOpen) {
        AlertDialog(
            onDismissRequest = viewModel::closeDialog,
            title = { Text(if (viewModel.editingCategory != null) "Editar categoria" else "Nova categoria") },
            text = {
                Column {
                    CustomTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it; viewModel.clearError() },
                        label = "Nome",
                        placeholder = "ex.: Trabalho",
                        isError = viewModel.errorMessage != null
                    )
                    viewModel.errorMessage?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::saveCategory) { Text("Salvar", color = PrimaryBlue) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeDialog) { Text("Cancelar") }
            }
        )
    }

    // Confirmação antes de DELETAR
    categoryToDelete?.let { category ->
        DeleteConfirmDialog(
            itemName = category.name,
            onConfirm = {
                viewModel.deleteCategory(category)
                categoryToDelete = null
            },
            onDismiss = { categoryToDelete = null }
        )
    }
}
