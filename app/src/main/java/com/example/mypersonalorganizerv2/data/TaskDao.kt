package com.example.mypersonalorganizerv2.data

import android.content.ContentValues
import android.database.Cursor
import com.example.mypersonalorganizerv2.model.Task

// DAO de TAREFAS — aqui está o CRUD 1 no banco de dados.
// C = insert | R = getAllByUser / getById | U = update | D = delete
class TaskDao(private val dbHelper: DatabaseHelper) {

    // CREATE (Criar): salva uma nova tarefa.
    fun insert(task: Task): Long {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.insert(DatabaseHelper.TABLE_TASKS, null, toContentValues(task))
    }

    // READ (Listar): busca todas as tarefas de um usuário.
    // Ordena: pendentes primeiro (done ASC) e as mais novas primeiro (id DESC).
    fun getAllByUser(userId: Long): List<Task> {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_TASKS} WHERE user_id = ? ORDER BY done ASC, id DESC",
            arrayOf(userId.toString())
        )
        val tasks = mutableListOf<Task>()
        // moveToNext() anda linha por linha até acabar o resultado
        while (cursor.moveToNext()) {
            tasks.add(cursorToTask(cursor))
        }
        cursor.close() // sempre fechar o cursor
        return tasks
    }

    // READ (Ler um): busca uma tarefa pelo id (usado na tela de edição).
    fun getById(id: Long): Task? {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_TASKS} WHERE id = ?",
            arrayOf(id.toString())
        )
        val task = if (cursor.moveToFirst()) cursorToTask(cursor) else null
        cursor.close()
        return task
    }

    // UPDATE (Alterar): atualiza uma tarefa que já existe.
    fun update(task: Task): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.update(
            DatabaseHelper.TABLE_TASKS,
            toContentValues(task),
            "id = ?",                    // qual tarefa alterar
            arrayOf(task.id.toString())  // valor que entra no lugar do "?"
        )
    }

    // DELETE (Deletar): apaga uma tarefa pelo id.
    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.delete(DatabaseHelper.TABLE_TASKS, "id = ?", arrayOf(id.toString()))
    }

    // Converte um objeto Task (Model) em ContentValues (coluna -> valor) para salvar no banco.
    private fun toContentValues(task: Task): ContentValues {
        return ContentValues().apply {
            put("user_id", task.userId)
            put("title", task.title)
            put("category", task.category)
            put("priority", task.priority)
            put("delivery_date", task.deliveryDate)
            put("notes", task.notes)
            put("done", if (task.done) 1 else 0) // true vira 1, false vira 0
        }
    }

    // Converte a linha atual do Cursor (resultado do banco) em um objeto Task (Model).
    private fun cursorToTask(cursor: Cursor): Task {
        return Task(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            category = cursor.getString(cursor.getColumnIndexOrThrow("category")) ?: "",
            priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")) ?: "",
            deliveryDate = cursor.getString(cursor.getColumnIndexOrThrow("delivery_date")) ?: "",
            notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")) ?: "",
            done = cursor.getInt(cursor.getColumnIndexOrThrow("done")) == 1 // 1 vira true
        )
    }
}
