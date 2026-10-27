package com.example.mypersonalorganizerv2.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mypersonalorganizerv2.data.AppointmentDao
import com.example.mypersonalorganizerv2.data.DatabaseHelper
import com.example.mypersonalorganizerv2.data.SessionManager
import com.example.mypersonalorganizerv2.model.Appointment

// VIEWMODEL dos COMPROMISSOS (CRUD 2).
// Funciona igual ao TaskViewModel, mas para compromissos.
class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val appointmentDao = AppointmentDao(DatabaseHelper(application)) // acesso à tabela de compromissos
    private val sessionManager = SessionManager(application)                // para saber quem está logado

    // Lista de compromissos mostrada na LazyColumn (a tela se atualiza sozinha quando muda)
    val appointments = mutableStateListOf<Appointment>()

    // Campos do formulário
    var serviceType by mutableStateOf("Consulta")
    var date by mutableStateOf("")
    var time by mutableStateOf("")
    var location by mutableStateOf("")
    var observations by mutableStateOf("")

    // Mensagem de erro do formulário (null = sem erro)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Compromisso em edição: null = CRIANDO | preenchido = EDITANDO
    var editingAppointment by mutableStateOf<Appointment?>(null)
        private set

    // Evita recarregar o formulário e perder o que o usuário digitou
    private var formLoaded = false

    // LISTAR: busca no banco os compromissos do usuário logado.
    fun loadAppointments() {
        appointments.clear()
        appointments.addAll(appointmentDao.getAllByUser(sessionManager.getUserId()))
    }

    // Prepara o formulário: -1 = novo | id real = carrega do banco para editar
    fun loadAppointment(appointmentId: Long) {
        if (formLoaded) return
        formLoaded = true

        val appointment = if (appointmentId > 0) appointmentDao.getById(appointmentId) else null
        editingAppointment = appointment
        if (appointment != null) {
            serviceType = appointment.serviceType
            date = appointment.date
            time = appointment.time
            location = appointment.location
            observations = appointment.observations
        }
    }

    // CRIAR ou ALTERAR: retorna true se salvou.
    fun saveAppointment(): Boolean {
        // Data e horário são obrigatórios
        if (date.isBlank() || time.isBlank()) {
            errorMessage = "Informe a data e o horário."
            return false
        }

        val current = editingAppointment
        if (current == null) {
            // CRIAR um novo compromisso
            appointmentDao.insert(
                Appointment(
                    userId = sessionManager.getUserId(), // dono = usuário logado
                    serviceType = serviceType,
                    date = date.trim(),
                    time = time.trim(),
                    location = location.trim(),
                    observations = observations.trim()
                )
            )
        } else {
            // ALTERAR o compromisso existente
            appointmentDao.update(
                current.copy(
                    serviceType = serviceType,
                    date = date.trim(),
                    time = time.trim(),
                    location = location.trim(),
                    observations = observations.trim()
                )
            )
        }
        errorMessage = null
        return true
    }

    // DELETAR: apaga o compromisso e recarrega a lista.
    fun deleteAppointment(appointment: Appointment) {
        appointmentDao.delete(appointment.id)
        loadAppointments()
    }

    // Apaga a mensagem de erro
    fun clearError() {
        errorMessage = null
    }
}
