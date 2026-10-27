package com.example.mypersonalorganizerv2.data

import android.content.ContentValues
import android.database.Cursor
import com.example.mypersonalorganizerv2.model.Appointment

// DAO de COMPROMISSOS — aqui está o CRUD 2 no banco de dados.
// C = insert | R = getAllByUser / getById | U = update | D = delete
class AppointmentDao(private val dbHelper: DatabaseHelper) {

    // CREATE (Criar): salva um novo compromisso.
    fun insert(appointment: Appointment): Long {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.insert(DatabaseHelper.TABLE_APPOINTMENTS, null, toContentValues(appointment))
    }

    // READ (Listar): busca todos os compromissos de um usuário (mais novos primeiro).
    fun getAllByUser(userId: Long): List<Appointment> {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_APPOINTMENTS} WHERE user_id = ? ORDER BY id DESC",
            arrayOf(userId.toString())
        )
        val appointments = mutableListOf<Appointment>()
        // percorre o resultado linha por linha
        while (cursor.moveToNext()) {
            appointments.add(cursorToAppointment(cursor))
        }
        cursor.close() // sempre fechar o cursor
        return appointments
    }

    // READ (Ler um): busca um compromisso pelo id (usado na tela de edição).
    fun getById(id: Long): Appointment? {
        val db = dbHelper.readableDatabase // abre o banco só para LER
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_APPOINTMENTS} WHERE id = ?",
            arrayOf(id.toString())
        )
        val appointment = if (cursor.moveToFirst()) cursorToAppointment(cursor) else null
        cursor.close()
        return appointment
    }

    // UPDATE (Alterar): atualiza um compromisso que já existe.
    fun update(appointment: Appointment): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.update(
            DatabaseHelper.TABLE_APPOINTMENTS,
            toContentValues(appointment),
            "id = ?",                          // qual compromisso alterar
            arrayOf(appointment.id.toString()) // valor que entra no lugar do "?"
        )
    }

    // DELETE (Deletar): apaga um compromisso pelo id.
    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase // abre o banco para ESCREVER
        return db.delete(DatabaseHelper.TABLE_APPOINTMENTS, "id = ?", arrayOf(id.toString()))
    }

    // Converte um objeto Appointment (Model) em ContentValues (coluna -> valor).
    private fun toContentValues(appointment: Appointment): ContentValues {
        return ContentValues().apply {
            put("user_id", appointment.userId)
            put("service_type", appointment.serviceType)
            put("date", appointment.date)
            put("time", appointment.time)
            put("location", appointment.location)
            put("observations", appointment.observations)
        }
    }

    // Converte a linha atual do Cursor em um objeto Appointment (Model).
    private fun cursorToAppointment(cursor: Cursor): Appointment {
        return Appointment(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
            serviceType = cursor.getString(cursor.getColumnIndexOrThrow("service_type")),
            date = cursor.getString(cursor.getColumnIndexOrThrow("date")) ?: "",
            time = cursor.getString(cursor.getColumnIndexOrThrow("time")) ?: "",
            location = cursor.getString(cursor.getColumnIndexOrThrow("location")) ?: "",
            observations = cursor.getString(cursor.getColumnIndexOrThrow("observations")) ?: ""
        )
    }
}
