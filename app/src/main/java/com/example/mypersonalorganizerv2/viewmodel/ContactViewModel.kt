package com.example.mypersonalorganizerv2.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypersonalorganizerv2.data.ContactDao
import com.example.mypersonalorganizerv2.data.DatabaseHelper
import com.example.mypersonalorganizerv2.data.ImageStorage
import com.example.mypersonalorganizerv2.data.SessionManager
import com.example.mypersonalorganizerv2.data.api.RetrofitClient
import com.example.mypersonalorganizerv2.model.Contact
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

// VIEWMODEL dos CONTATOS (CRUD 4).
// Além do banco, usa a API ViaCEP (Retrofit) e a galeria (foto do contato).
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactDao = ContactDao(DatabaseHelper(application)) // acesso à tabela de contatos
    private val sessionManager = SessionManager(application)        // para saber quem está logado

    // Id do usuário logado: a tela usa como chave do LaunchedEffect
    val userId = sessionManager.getUserId()

    // Lista mostrada na LazyColumn
    val contacts = mutableStateListOf<Contact>()

    // Campos do formulário
    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var cep by mutableStateOf("")
    var address by mutableStateOf("")
    var photo by mutableStateOf("")
        private set

    // Mensagem de erro do formulário (null = sem erro)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // true enquanto espera a resposta da API (desabilita o botão "Buscar")
    var searchingCep by mutableStateOf(false)
        private set

    // Contato em edição: null = CRIANDO | preenchido = EDITANDO
    var editingContact by mutableStateOf<Contact?>(null)
        private set

    // Evita recarregar o formulário e perder o que o usuário digitou
    private var formLoaded = false

    // LISTAR: busca no banco os contatos do usuário logado.
    fun loadContacts() {
        contacts.clear()
        contacts.addAll(contactDao.getAllByUser(userId))
    }

    // Prepara o formulário: -1 = novo | id real = carrega do banco para editar
    fun loadContact(contactId: Long) {
        if (formLoaded) return
        formLoaded = true

        val contact = if (contactId > 0) contactDao.getById(contactId) else null
        editingContact = contact
        if (contact != null) {
            name = contact.name
            phone = contact.phone
            email = contact.email
            cep = contact.cep
            address = contact.address
            photo = contact.photo
        }
    }

    // FOTO escolhida na galeria: copia para a pasta do app.
    // Só vai para o banco quando o usuário tocar em "Salvar".
    fun onPhotoPicked(uri: Uri) {
        // Se já tinha escolhido outra foto ainda não salva, apaga a anterior
        if (photo != editingContact?.photo) ImageStorage.deleteImage(photo)
        photo = ImageStorage.saveImage(getApplication(), uri)
    }

    // API (Retrofit): busca o endereço pelo CEP na ViaCEP e preenche o campo "Endereço".
    fun searchCep() {
        val digits = cep.filter { it.isDigit() } // aceita "80000-000" ou "80000000"
        if (digits.length != 8) {
            errorMessage = "O CEP deve ter 8 números."
            return
        }
        searchingCep = true
        errorMessage = null

        // viewModelScope.launch: a chamada de internet roda numa coroutine (não trava a tela)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.viaCepApi.buscarCep(digits)
                if (response.erro == true) {
                    errorMessage = "CEP não encontrado."
                } else {
                    // Junta só as partes que vieram preenchidas, ex.: "Rua X, Centro, Curitiba - PR"
                    val city = listOfNotNull(response.localidade, response.uf).filter { it.isNotBlank() }.joinToString(" - ")
                    address = listOfNotNull(response.logradouro, response.bairro, city)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                }
            } catch (e: CancellationException) {
                throw e // a tela foi fechada: deixa a coroutine ser cancelada
            } catch (e: Exception) {
                errorMessage = "Não foi possível buscar o CEP. Verifique a internet."
            } finally {
                searchingCep = false
            }
        }
    }

    // CRIAR ou ALTERAR: retorna true se salvou.
    fun saveContact(): Boolean {
        errorMessage = when {
            name.isBlank() -> "Informe o nome do contato."
            email.isNotBlank() && !email.contains("@") -> "Informe um e-mail válido."
            else -> null
        }
        if (errorMessage != null) return false

        val current = editingContact
        if (current == null) {
            // CRIAR
            contactDao.insert(
                Contact(
                    userId = userId,
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email.trim().lowercase(),
                    cep = cep.trim(),
                    address = address.trim(),
                    photo = photo
                )
            )
        } else {
            // ALTERAR: se trocou a foto, apaga a antiga do celular
            if (current.photo != photo) ImageStorage.deleteImage(current.photo)
            contactDao.update(
                current.copy(
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email.trim().lowercase(),
                    cep = cep.trim(),
                    address = address.trim(),
                    photo = photo
                )
            )
        }
        return true
    }

    // DELETAR: apaga o contato do banco e a foto dele do celular.
    fun deleteContact(contact: Contact) {
        contactDao.delete(contact.id)
        ImageStorage.deleteImage(contact.photo)
        loadContacts()
    }

    fun clearError() {
        errorMessage = null
    }
}
