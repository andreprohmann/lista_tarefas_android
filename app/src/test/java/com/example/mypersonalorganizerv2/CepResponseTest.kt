package com.example.mypersonalorganizerv2

import com.example.mypersonalorganizerv2.data.api.CepResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

// Confere se o Gson (usado pelo Retrofit) lê as respostas REAIS da ViaCEP.
class CepResponseTest {

    @Test
    fun cepInexistente_viaCepMandaErroComoTexto() {
        // A ViaCEP responde exatamente assim para um CEP que não existe
        val response = Gson().fromJson("""{ "erro": "true" }""", CepResponse::class.java)
        assertEquals(true, response.erro)
    }

    @Test
    fun cepValido_preencheOsCampos() {
        val json = """{ "cep": "01001-000", "logradouro": "Praça da Sé", "bairro": "Sé", "localidade": "São Paulo", "uf": "SP" }"""
        val response = Gson().fromJson(json, CepResponse::class.java)
        assertNotEquals(true, response.erro)
        assertEquals("Praça da Sé", response.logradouro)
        assertEquals("SP", response.uf)
    }
}
