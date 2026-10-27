package com.example.mypersonalorganizerv2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mypersonalorganizerv2.data.DatabaseHelper
import com.example.mypersonalorganizerv2.data.SessionManager
import com.example.mypersonalorganizerv2.data.UserDao
import com.example.mypersonalorganizerv2.model.User

// VIEWMODEL do PERFIL: carrega e salva os dados do usuário logado.
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = UserDao(DatabaseHelper(application)) // acesso à tabela de usuários
    private val sessionManager = SessionManager(application)  // para saber quem está logado

    // Usuário carregado do banco (guardado para usar na hora de salvar)
    private var user: User? = null

    // Campos mostrados na tela de perfil
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")

    // Mensagem mostrada após salvar (sucesso ou erro)
    var message by mutableStateOf<String?>(null)
        private set
    // true = a mensagem é de erro (aparece em vermelho)
    var isError by mutableStateOf(false)
        private set

    // Busca no banco os dados do usuário logado e preenche os campos.
    fun loadUser() {
        val loaded = userDao.findById(sessionManager.getUserId()) ?: return // se não achar, não faz nada
        user = loaded
        name = loaded.name
        email = loaded.email
        phone = loaded.phone
        address = loaded.address
    }

    // Salva as alterações do perfil no banco.
    fun saveUser() {
        val current = user ?: return
        val normalizedEmail = email.trim().lowercase()
        val otherUser = userDao.findByEmail(normalizedEmail) // alguém já usa esse e-mail?

        // Validações
        val error = when {
            name.isBlank() -> "Informe seu nome."
            normalizedEmail.isBlank() || !normalizedEmail.contains("@") -> "Informe um e-mail válido."
            otherUser != null && otherUser.id != current.id -> "Este e-mail já está em uso."
            else -> null
        }
        if (error != null) {
            isError = true
            message = error
            return
        }

        // Copia o usuário trocando só os campos editados e salva no banco
        val updated = current.copy(
            name = name.trim(),
            email = normalizedEmail,
            phone = phone.trim(),
            address = address.trim()
        )
        userDao.update(updated)
        user = updated
        isError = false
        message = "Dados salvos com sucesso."
    }

    // Apaga a mensagem (chamado quando o usuário volta a digitar)
    fun clearMessage() {
        message = null
    }
}
