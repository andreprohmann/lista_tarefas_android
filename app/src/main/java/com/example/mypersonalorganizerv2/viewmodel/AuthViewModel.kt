package com.example.mypersonalorganizerv2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mypersonalorganizerv2.data.CategoryDao
import com.example.mypersonalorganizerv2.data.DatabaseHelper
import com.example.mypersonalorganizerv2.data.SessionManager
import com.example.mypersonalorganizerv2.data.UserDao
import com.example.mypersonalorganizerv2.model.Category
import com.example.mypersonalorganizerv2.model.User

// VIEWMODEL de autenticação: cuida do LOGIN, do CADASTRO e do SAIR.
// A tela só mostra os campos; quem confere os dados e fala com o banco é este ViewModel.
// Usamos AndroidViewModel porque ele dá acesso ao "application" (contexto),
// que é necessário para abrir o banco e a sessão.
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = UserDao(DatabaseHelper(application)) // acesso à tabela de usuários
    private val sessionManager = SessionManager(application)  // controle de sessão
    private val categoryDao = CategoryDao(DatabaseHelper(application)) // categorias iniciais do novo usuário

    // Campos digitados nas telas de login e cadastro.
    // mutableStateOf faz a tela se atualizar sozinha quando o valor muda.
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var birthDate by mutableStateOf("")
    var password by mutableStateOf("")

    // Mensagem de erro mostrada na tela (null = sem erro).
    // "private set" = só o ViewModel pode mudar esse valor.
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Diz se já existe alguém logado (usado para pular a tela de login).
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    // LOGIN: retorna true se deu certo, false se deu errado.
    fun login(): Boolean {
        // 1) Confere se os campos foram preenchidos
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Informe e-mail e senha."
            return false
        }

        // 2) Procura no banco um usuário com esse e-mail e essa senha
        val user = userDao.findByEmailAndPassword(email.trim().lowercase(), password)
        if (user == null) {
            errorMessage = "E-mail ou senha incorretos."
            return false
        }

        // 3) Encontrou: salva o id na sessão (o usuário agora está logado)
        sessionManager.saveUserId(user.id)
        errorMessage = null
        return true
    }

    // CADASTRO: retorna true se a conta foi criada.
    fun register(): Boolean {
        // E-mail sempre em minúsculas, para "Joao@x.com" e "joao@x.com" serem iguais
        val normalizedEmail = email.trim().lowercase()

        // 1) Validações: se alguma falhar, guarda a mensagem de erro
        errorMessage = when {
            name.isBlank() -> "Informe seu nome."
            normalizedEmail.isBlank() || !normalizedEmail.contains("@") -> "Informe um e-mail válido."
            password.length < 4 -> "A senha deve ter pelo menos 4 caracteres."
            userDao.findByEmail(normalizedEmail) != null -> "Já existe uma conta com este e-mail."
            else -> null // tudo certo
        }
        if (errorMessage != null) return false

        // 2) Salva o novo usuário no banco
        val id = userDao.insert(
            User(
                name = name.trim(),
                email = normalizedEmail,
                phone = phone.trim(),
                birthDate = birthDate.trim(),
                password = password
            )
        )
        // insert retorna -1 quando dá erro ao salvar
        if (id == -1L) {
            errorMessage = "Não foi possível criar a conta."
            return false
        }

        // 3) Cria as categorias padrão do novo usuário (ele pode alterar depois)
        listOf("Pessoal", "Trabalho", "Estudos", "Saúde", "Outros").forEach { name ->
            categoryDao.insert(Category(userId = id, name = name))
        }
        return true
    }

    // SAIR: encerra a sessão.
    fun logout() {
        sessionManager.logout()
    }

    // Apaga a mensagem de erro (chamado quando o usuário volta a digitar).
    fun clearError() {
        errorMessage = null
    }
}
