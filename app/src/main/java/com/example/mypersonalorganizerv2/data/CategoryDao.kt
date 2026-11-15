package com.example.mypersonalorganizerv2.data

import android.content.ContentValues
import android.database.Cursor
import com.example.mypersonalorganizerv2.model.Category

// DAO de CATEGORIAS — aqui está o CRUD 3 no banco de dados.
// C = insert | R = getAllByUser / findByName | U = update | D = delete
class CategoryDao(private val dbHelper: DatabaseHelper) {

    // CREATE (Criar): salva uma nova categoria.
    fun insert(category: Category): Long {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.insert(DatabaseHelper.TABLE_CATEGORIES, null, toContentValues(category))
    }

    // READ (Listar): busca as categorias do usuário em ordem alfabética.
    fun getAllByUser(userId: Long): List<Category> {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CATEGORIES} WHERE user_id = ? ORDER BY name COLLATE NOCASE",
            arrayOf(userId.toString())
        )
        val categories = mutableListOf<Category>()
        while (cursor.moveToNext()) {
            categories.add(cursorToCategory(cursor))
        }
        cursor.close()
        return categories
    }

    // Procura uma categoria do usuário pelo nome (para não deixar cadastrar repetida).
    fun findByName(userId: Long, name: String): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CATEGORIES} WHERE user_id = ? AND name = ? COLLATE NOCASE",
            arrayOf(userId.toString(), name)
        )
        val category = if (cursor.moveToFirst()) cursorToCategory(cursor) else null
        cursor.close()
        return category
    }

    // UPDATE (Alterar): renomeia a categoria.
    // As tarefas guardam o NOME da categoria, então elas também são renomeadas.
    fun update(old: Category, newName: String): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        db.update(
            DatabaseHelper.TABLE_TASKS,
            ContentValues().apply { put("category", newName) },
            "user_id = ? AND category = ?",
            arrayOf(old.userId.toString(), old.name)
        )
        return db.update(
            DatabaseHelper.TABLE_CATEGORIES,
            toContentValues(old.copy(name = newName)),
            "id = ?",
            arrayOf(old.id.toString())
        )
    }

    // DELETE (Deletar): apaga a categoria pelo id.
    // As tarefas que já usavam essa categoria continuam com o nome antigo.
    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.delete(DatabaseHelper.TABLE_CATEGORIES, "id = ?", arrayOf(id.toString()))
    }

    // Converte um objeto Category (Model) em ContentValues (coluna -> valor).
    private fun toContentValues(category: Category): ContentValues {
        return ContentValues().apply {
            put("user_id", category.userId)
            put("name", category.name)
        }
    }

    // Converte a linha atual do Cursor em um objeto Category (Model).
    private fun cursorToCategory(cursor: Cursor): Category {
        return Category(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        )
    }
}
