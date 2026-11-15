package com.example.mypersonalorganizerv2.data

import android.content.ContentValues
import android.database.Cursor
import com.example.mypersonalorganizerv2.model.Contact

// DAO de CONTATOS — aqui está o CRUD 4 no banco de dados.
// C = insert | R = getAllByUser / getById | U = update | D = delete
class ContactDao(private val dbHelper: DatabaseHelper) {

    // CREATE (Criar): salva um novo contato.
    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.insert(DatabaseHelper.TABLE_CONTACTS, null, toContentValues(contact))
    }

    // READ (Listar): busca os contatos do usuário em ordem alfabética.
    fun getAllByUser(userId: Long): List<Contact> {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CONTACTS} WHERE user_id = ? ORDER BY name COLLATE NOCASE",
            arrayOf(userId.toString())
        )
        val contacts = mutableListOf<Contact>()
        while (cursor.moveToNext()) {
            contacts.add(cursorToContact(cursor))
        }
        cursor.close()
        return contacts
    }

    // READ (Ler um): busca um contato pelo id (usado na tela de edição).
    fun getById(id: Long): Contact? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CONTACTS} WHERE id = ?",
            arrayOf(id.toString())
        )
        val contact = if (cursor.moveToFirst()) cursorToContact(cursor) else null
        cursor.close()
        return contact
    }

    // UPDATE (Alterar): atualiza um contato que já existe.
    fun update(contact: Contact): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.update(
            DatabaseHelper.TABLE_CONTACTS,
            toContentValues(contact),
            "id = ?",
            arrayOf(contact.id.toString())
        )
    }

    // DELETE (Deletar): apaga o contato pelo id.
    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.delete(DatabaseHelper.TABLE_CONTACTS, "id = ?", arrayOf(id.toString()))
    }

    // Converte um objeto Contact (Model) em ContentValues (coluna -> valor).
    private fun toContentValues(contact: Contact): ContentValues {
        return ContentValues().apply {
            put("user_id", contact.userId)
            put("name", contact.name)
            put("phone", contact.phone)
            put("email", contact.email)
            put("cep", contact.cep)
            put("address", contact.address)
            put("photo", contact.photo)
        }
    }

    // Converte a linha atual do Cursor em um objeto Contact (Model).
    private fun cursorToContact(cursor: Cursor): Contact {
        return Contact(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")) ?: "",
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: "",
            cep = cursor.getString(cursor.getColumnIndexOrThrow("cep")) ?: "",
            address = cursor.getString(cursor.getColumnIndexOrThrow("address")) ?: "",
            photo = cursor.getString(cursor.getColumnIndexOrThrow("photo")) ?: ""
        )
    }
}
