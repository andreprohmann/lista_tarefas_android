package com.example.mypersonalorganizerv2.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mypersonalorganizerv2.data.ImageStorage
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

// FOTO redonda. Se "onImagePicked" for informado, ao tocar abre a GALERIA do celular
// (recurso nativo) para escolher uma imagem. Sem ele, só mostra a foto (ex.: na lista).
@Composable
fun PhotoPicker(
    photoPath: String,                        // caminho da foto salva ("" = sem foto)
    size: Dp = 120.dp,
    onImagePicked: ((Uri) -> Unit)? = null    // o que fazer com a imagem escolhida
) {
    // Abre o seletor de fotos do Android. Não precisa pedir permissão ao usuário.
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onImagePicked?.invoke(uri) // uri = null quando o usuário cancela
    }

    // Lê a foto do arquivo só quando o caminho muda (remember evita ler a cada recomposição)
    val bitmap = remember(photoPath) { ImageStorage.loadBitmap(photoPath) }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFF0F0F0))
            .clickable(enabled = onImagePicked != null) {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(size * 2 / 3), tint = Color.LightGray)
        }
        // Selo da câmera: indica que dá para trocar a foto
        if (onImagePicked != null) {
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).size(32.dp).clip(CircleShape).background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Escolher foto da galeria", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
