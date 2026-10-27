package com.example.mypersonalorganizerv2.data

import android.content.ContentValues
import android.database.Cursor
import com.example.mypersonalorganizerv2.model.User

// DAO (Data Access Object) de USUÁRIOS.
// É a classe que conversa com a tabela "users" do banco.
// Toda a parte de SQL dos usuários fica aqui.
class UserDao(private val dbHelper: DatabaseHelper) {

    // CADASTRO: salva um novo usuário no banco.
    // Retorna o id gerado, ou -1 se der erro.
    fun insert(user: User): Long {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER

        // ContentValues é como um "mapa": nome da coluna -> valor
        val values = ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("phone", user.phone)
            put("birth_date", user.birthDate)
            put("address", user.address)
            put("password", user.password)
        }
        return db.insert(DatabaseHelper.TABLE_USERS, null, values)
    }

    // PERFIL: atualiza os dados de um usuário que já existe.
    // Retorna quantas linhas foram alteradas.
    fun update(user: User): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        val values = ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("phone", user.phone)
            put("birth_date", user.birthDate)
            put("address", user.address)
            put("password", user.password)
        }
        // "id = ?" diz QUAL usuário alterar. O "?" é trocado pelo valor do arrayOf.
        return db.update(DatabaseHelper.TABLE_USERS, values, "id = ?", arrayOf(user.id.toString()))
    }

    // Busca um usuário pelo id (usado para carregar o perfil).
    // Retorna null se não encontrar.
    fun findById(id: Long): User? {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE id = ?",
            arrayOf(id.toString())
        )
        // moveToFirst() vai para a primeira linha do resultado (se existir)
        val user = if (cursor.moveToFirst()) cursorToUser(cursor) else null
        cursor.close() // sempre fechar o cursor para liberar memória
        return user
    }

    // Busca um usuário pelo e-mail (usado para não deixar cadastrar e-mail repetido).
    fun findByEmail(email: String): User? {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE email = ?",
            arrayOf(email)
        )
        val user = if (cursor.moveToFirst()) cursorToUser(cursor) else null
        cursor.close()
        return user
    }

    // LOGIN: procura um usuário com esse e-mail E essa senha.
    // Se encontrar, o login está correto. Se retornar null, e-mail ou senha estão errados.
    fun findByEmailAndPassword(email: String, password: String): User? {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )
        val user = if (cursor.moveToFirst()) cursorToUser(cursor) else null
        cursor.close()
        return user
    }

    // Transforma a linha atual do Cursor (resultado da consulta) em um objeto User (Model).
    private fun cursorToUser(cursor: Cursor): User {
        return User(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")) ?: "",          // "?: """ = se vier vazio do banco, usa texto vazio
            birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birth_date")) ?: "",
            address = cursor.getString(cursor.getColumnIndexOrThrow("address")) ?: "",
            password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
        )
    }
}
