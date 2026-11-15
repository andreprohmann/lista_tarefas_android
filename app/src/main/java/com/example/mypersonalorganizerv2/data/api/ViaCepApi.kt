package com.example.mypersonalorganizerv2.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// MODEL da resposta da API ViaCEP.
// Os nomes dos campos são iguais aos do JSON, assim o Gson preenche sozinho.
// Exemplo: https://viacep.com.br/ws/01001000/json/
data class CepResponse(
    val cep: String?,
    val logradouro: String?, // rua
    val bairro: String?,
    val localidade: String?, // cidade
    val uf: String?,         // estado
    val erro: Boolean?       // a API devolve "erro": true quando o CEP não existe
)

// INTERFACE do Retrofit: cada função é uma rota da API.
// "suspend" = a chamada roda numa coroutine e não trava a tela.
interface ViaCepApi {
    @GET("ws/{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): CepResponse
}

// Cria o Retrofit UMA vez só (object = singleton) e reaproveita em todo o app.
object RetrofitClient {
    private const val BASE_URL = "https://viacep.com.br/"

    val viaCepApi: ViaCepApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // JSON -> CepResponse
        .build()
        .create(ViaCepApi::class.java)
}
