package com.example.mypersonalorganizerv2.model

// MODEL do compromisso (CRUD 2).
// É a "ficha" de um compromisso. Cada campo é uma coluna da tabela "appointments".
data class Appointment(
    val id: Long = 0,           // número único do compromisso (o banco gera sozinho)
    val userId: Long,           // id do usuário dono do compromisso
    val serviceType: String,    // tipo, ex.: "Consulta", "Reunião"
    val date: String,           // data, ex.: "10/11/2026"
    val time: String,           // horário, ex.: "14:00"
    val location: String,       // local
    val observations: String    // observações
)
