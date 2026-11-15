package com.example.mypersonalorganizerv2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mypersonalorganizerv2.data.CategoryDao
import com.example.mypersonalorganizerv2.data.DatabaseHelper
import com.example.mypersonalorganizerv2.data.SessionManager
import com.example.mypersonalorganizerv2.model.Category

// VIEWMODEL das CATEGORIAS (CRUD 3).
// Criar e editar acontecem numa janela (AlertDialog) na própria tela da lista.
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryDao = CategoryDao(DatabaseHelper(application)) // acesso à tabela de categorias
    private val sessionManager = SessionManager(application)          // para saber quem está logado

    // Id do usuário logado: a tela usa como chave do LaunchedEffect
    val userId = sessionManager.getUserId()

    // Lista mostrada na LazyColumn (a tela se atualiza sozinha quando muda)
    val categories = mutableStateListOf<Category>()

    // Janela de criar/editar
    var dialogOpen by mutableStateOf(false)
        private set
    var name by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Categoria em edição: null = CRIANDO | preenchida = EDITANDO
    var editingCategory by mutableStateOf<Category?>(null)
        private set

    // LISTAR: busca no banco as categorias do usuário logado.
    fun loadCategories() {
        categories.clear()
        categories.addAll(categoryDao.getAllByUser(userId))
    }

    // Abre a janela vazia (nova categoria)
    fun openNew() {
        editingCategory = null
        name = ""
        errorMessage = null
        dialogOpen = true
    }

    // Abre a janela já preenchida (editar categoria)
    fun openEdit(category: Category) {
        editingCategory = category
        name = category.name
        errorMessage = null
        dialogOpen = true
    }

    fun closeDialog() {
        dialogOpen = false
    }

    // CRIAR ou ALTERAR
    fun saveCategory() {
        val trimmed = name.trim()
        val existing = categoryDao.findByName(userId, trimmed) // já existe uma com esse nome?
        val current = editingCategory

        errorMessage = when {
            trimmed.isBlank() -> "Informe o nome da categoria."
            existing != null && existing.id != current?.id -> "Já existe uma categoria com esse nome."
            else -> null
        }
        if (errorMessage != null) return

        if (current == null) {
            categoryDao.insert(Category(userId = userId, name = trimmed)) // CRIAR
        } else {
            categoryDao.update(current, trimmed)                          // ALTERAR
        }
        dialogOpen = false
        loadCategories() // recarrega a lista com a mudança
    }

    // DELETAR
    fun deleteCategory(category: Category) {
        categoryDao.delete(category.id)
        loadCategories()
    }

    fun clearError() {
        errorMessage = null
    }
}
