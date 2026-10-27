package com.example.mypersonalorganizerv2.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Classe que CRIA e GERENCIA o banco de dados SQLite do app.
// Ela herda de SQLiteOpenHelper (requisito do trabalho).
// Recebe: o contexto do app, o nome do arquivo do banco e a versão do banco.
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // onCreate roda UMA ÚNICA VEZ: na primeira vez que o app usa o banco.
    // Aqui criamos todas as tabelas.
    override fun onCreate(db: SQLiteDatabase) {

        // Tabela de USUÁRIOS (usada no login e no cadastro)
        // "email UNIQUE" impede que dois usuários tenham o mesmo e-mail.
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                phone TEXT,
                birth_date TEXT,
                address TEXT,
                password TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Tabela de TAREFAS (CRUD 1)
        // "user_id" guarda de quem é a tarefa, para cada usuário ver só as suas.
        // "done" é 0 (pendente) ou 1 (concluída), pois o SQLite não tem tipo booleano.
        db.execSQL(
            """
            CREATE TABLE $TABLE_TASKS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                category TEXT,
                priority TEXT,
                delivery_date TEXT,
                notes TEXT,
                done INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id)
            )
            """.trimIndent()
        )

        // Tabela de COMPROMISSOS (CRUD 2)
        // Também tem "user_id" para separar os compromissos de cada usuário.
        db.execSQL(
            """
            CREATE TABLE $TABLE_APPOINTMENTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                service_type TEXT NOT NULL,
                date TEXT,
                time TEXT,
                location TEXT,
                observations TEXT,
                FOREIGN KEY (user_id) REFERENCES $TABLE_USERS(id)
            )
            """.trimIndent()
        )
    }

    // onUpgrade roda quando aumentamos o DATABASE_VERSION.
    // Aqui, de forma simples, apagamos as tabelas antigas e criamos de novo.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Constantes: nomes fixos usados em vários lugares do código.
    companion object {
        const val DATABASE_NAME = "personal_organizer.db" // nome do arquivo do banco no celular
        const val DATABASE_VERSION = 1                    // versão do banco

        // Nomes das tabelas
        const val TABLE_USERS = "users"
        const val TABLE_TASKS = "tasks"
        const val TABLE_APPOINTMENTS = "appointments"
    }
}
