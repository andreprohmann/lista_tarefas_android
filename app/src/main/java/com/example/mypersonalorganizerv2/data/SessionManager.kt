package com.example.mypersonalorganizerv2.data

import android.content.Context

// CONTROLE DE SESSÃO: lembra QUEM está logado no app.
// Funciona como uma "pulseira de festa": ao entrar a pessoa recebe, ao sair ela é retirada.
// Guarda o id do usuário no SharedPreferences (um armazenamento simples de chave -> valor
// do próprio Android), então a sessão continua mesmo depois de fechar o app.
class SessionManager(context: Context) {

    // Abre (ou cria) o arquivo de preferências chamado "session"
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Chamado no LOGIN: guarda o id do usuário que entrou.
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    // Retorna o id do usuário logado (ou -1 se ninguém estiver logado).
    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, NO_USER)

    // Retorna true se existe alguém logado.
    fun isLoggedIn(): Boolean = getUserId() != NO_USER

    // Chamado no SAIR: apaga o id salvo, encerrando a sessão.
    fun logout() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    // Constantes usadas só aqui dentro
    companion object {
        private const val PREFS_NAME = "session"   // nome do arquivo de preferências
        private const val KEY_USER_ID = "user_id"  // chave onde o id fica salvo
        private const val NO_USER = -1L            // valor que significa "ninguém logado"
    }
}
