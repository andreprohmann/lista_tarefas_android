package com.example.mypersonalorganizerv2.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Guarda e lê as FOTOS escolhidas na galeria (recurso nativo do celular).
// A galeria devolve um endereço (Uri) que pode deixar de funcionar depois,
// então copiamos a imagem para a pasta interna do app e salvamos o CAMINHO no banco.
object ImageStorage {

    // Copia a imagem da galeria para a pasta do app e devolve o caminho do arquivo.
    fun saveImage(context: Context, uri: Uri): String {
        val file = File(context.filesDir, "foto_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file.absolutePath
    }

    // Apaga a foto antiga quando o usuário troca por outra (não deixa lixo no celular).
    fun deleteImage(path: String) {
        if (path.isNotBlank()) File(path).delete()
    }

    // Lê a foto já reduzida (fotos de câmera são enormes e podem travar o app).
    fun loadBitmap(path: String, maxSize: Int = 512): Bitmap? {
        if (path.isBlank() || !File(path).exists()) return null
        // 1º passo: só descobre largura e altura, sem carregar a imagem
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        // 2º passo: carrega pulando pixels (inSampleSize = 2, 4, 8...) até ficar perto de maxSize
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= maxSize && bounds.outHeight / (sample * 2) >= maxSize) {
            sample *= 2
        }
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
    }
}
