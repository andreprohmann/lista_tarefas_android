package com.example.mypersonalorganizerv2.model

// MODEL de um contato (CRUD 4).
// Cada campo corresponde a uma coluna da tabela "contacts".
data class Contact(
    val id: Long = 0,      // número único (o banco gera sozinho)
    val userId: Long,      // dono do contato (usuário logado)
    val name: String,      // nome do contato
    val phone: String,     // telefone
    val email: String,     // e-mail
    val cep: String,       // CEP (usado para buscar o endereço na API ViaCEP)
    val address: String,   // endereço completo
    val photo: String = "" // caminho da foto escolhida na galeria ("" = sem foto)
)
