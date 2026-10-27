package com.example.mypersonalorganizerv2.model

// MODEL do usuário.
// É a "ficha" de uma pessoa cadastrada no app.
// Cada campo aqui corresponde a uma coluna da tabela "users" no banco.
data class User(
    val id: Long = 0,          // número único do usuário (o banco gera sozinho)
    val name: String,          // nome completo
    val email: String,         // e-mail usado para entrar no app
    val phone: String,         // telefone
    val birthDate: String,     // data de nascimento (texto, ex.: "10/05/2000")
    val address: String = "",  // endereço (preenchido na tela de perfil)
    val password: String       // senha usada no login
)
