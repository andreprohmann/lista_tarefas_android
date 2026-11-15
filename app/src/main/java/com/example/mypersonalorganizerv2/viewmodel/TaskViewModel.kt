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
import com.example.mypersonalorganizerv2.data.TaskDao
import com.example.mypersonalorganizerv2.model.Task

// VIEWMODEL das TAREFAS (CRUD 1).
// Fica entre as telas de tarefa e o banco:
// a tela pede ("salvar", "apagar"...), o ViewModel confere e chama o TaskDao.
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = TaskDao(DatabaseHelper(application)) // acesso à tabela de tarefas
    private val categoryDao = CategoryDao(DatabaseHelper(application)) // categorias do campo "Categoria"
    private val sessionManager = SessionManager(application)  // para saber quem está logado

    // Id do usuário logado: as telas usam como chave do LaunchedEffect,
    // pois os dados mostrados dependem de QUEM está logado.
    val userId = sessionManager.getUserId()

    // Lista de tarefas mostrada na LazyColumn.
    // mutableStateListOf: quando a lista muda, a tela se atualiza sozinha.
    val tasks = mutableStateListOf<Task>()

    // Campos do formulário (já começam com valores padrão)
    var title by mutableStateOf("")
    var category by mutableStateOf("")
    var priority by mutableStateOf("Média")
    var deliveryDate by mutableStateOf("")
    var notes by mutableStateOf("")

    // Opções do campo "Categoria": vêm da tabela de categorias (CRUD 3)
    val categories = mutableStateListOf<String>()

    // Mensagem de erro do formulário (null = sem erro)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Tarefa que está sendo editada.
    // null = estamos CRIANDO uma nova | preenchido = estamos EDITANDO
    var editingTask by mutableStateOf<Task?>(null)
        private set

    // Evita recarregar o formulário do banco e perder o que o usuário já digitou
    private var formLoaded = false

    // LISTAR: busca no banco as tarefas do usuário logado.
    fun loadTasks() {
        tasks.clear()
        tasks.addAll(taskDao.getAllByUser(sessionManager.getUserId()))
    }

    // Prepara o formulário.
    // taskId = -1 -> nova tarefa (formulário vazio)
    // taskId > 0  -> busca a tarefa no banco e preenche os campos (edição)
    fun loadTask(taskId: Long) {
        if (formLoaded) return
        formLoaded = true

        categories.clear()
        categories.addAll(categoryDao.getAllByUser(sessionManager.getUserId()).map { it.name })
        category = categories.firstOrNull() ?: "" // nova tarefa começa com a primeira categoria

        val task = if (taskId > 0) taskDao.getById(taskId) else null
        editingTask = task
        if (task != null) {
            title = task.title
            category = task.category
            priority = task.priority
            deliveryDate = task.deliveryDate
            notes = task.notes
        }
    }

    // CRIAR ou ALTERAR: retorna true se salvou.
    fun saveTask(): Boolean {
        // O título é obrigatório
        if (title.isBlank()) {
            errorMessage = "Informe o título da tarefa."
            return false
        }

        val current = editingTask
        if (current == null) {
            // CRIAR: não tem tarefa em edição, então é uma nova
            taskDao.insert(
                Task(
                    userId = sessionManager.getUserId(), // dono = usuário logado
                    title = title.trim(),
                    category = category,
                    priority = priority,
                    deliveryDate = deliveryDate.trim(),
                    notes = notes.trim()
                )
            )
        } else {
            // ALTERAR: copia a tarefa existente trocando só os campos editados
            taskDao.update(
                current.copy(
                    title = title.trim(),
                    category = category,
                    priority = priority,
                    deliveryDate = deliveryDate.trim(),
                    notes = notes.trim()
                )
            )
        }
        errorMessage = null
        return true
    }

    // ALTERAR: marca ou desmarca a tarefa como concluída (checkbox da lista).
    fun toggleDone(task: Task) {
        taskDao.update(task.copy(done = !task.done))
        loadTasks() // recarrega a lista para mostrar a mudança
    }

    // DELETAR: apaga a tarefa do banco.
    fun deleteTask(task: Task) {
        taskDao.delete(task.id)
        loadTasks() // recarrega a lista sem a tarefa apagada
    }

    // Apaga a mensagem de erro
    fun clearError() {
        errorMessage = null
    }
}
