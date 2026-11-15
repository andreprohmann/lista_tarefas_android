package com.example.mypersonalorganizerv2.model

// MODEL de uma categoria de tarefa (CRUD 3).
// Cada campo corresponde a uma coluna da tabela "categories".
data class Category(
    val id: Long = 0,   // número único (o banco gera sozinho)
    val userId: Long,   // dono da categoria (usuário logado)
    val name: String    // nome, ex.: "Trabalho"
)
