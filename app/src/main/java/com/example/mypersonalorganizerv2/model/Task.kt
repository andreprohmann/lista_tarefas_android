package com.example.mypersonalorganizerv2.model

// MODEL da tarefa (CRUD 1).
// É a "ficha" de uma tarefa. Cada campo é uma coluna da tabela "tasks".
data class Task(
    val id: Long = 0,           // número único da tarefa (o banco gera sozinho)
    val userId: Long,           // id do usuário dono da tarefa
    val title: String,          // título, ex.: "Estudar Android"
    val category: String,       // categoria, ex.: "Estudos"
    val priority: String,       // prioridade: "Alta", "Média" ou "Baixa"
    val deliveryDate: String,   // data de entrega
    val notes: String,          // anotações
    val done: Boolean = false   // true = tarefa concluída (no banco vira 1 ou 0)
)
