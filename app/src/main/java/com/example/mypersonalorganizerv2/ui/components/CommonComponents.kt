package com.example.mypersonalorganizerv2.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

// Campo de SELEÇÃO (lista suspensa).
// Usado em "Categoria" (tarefas) e "Tipo de serviço" (compromissos).
// Ao tocar, abre um menu com as opções; ao escolher, chama onSelect.
@Composable
fun SelectField(
    label: String,                // texto acima do campo
    selected: String,             // opção escolhida no momento
    options: List<String>,        // todas as opções disponíveis
    onSelect: (String) -> Unit    // o que fazer quando escolher uma opção
) {
    // Guarda se o menu está aberto (true) ou fechado (false)
    var expanded by remember { mutableStateOf(false) }

    Text(label, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
    Box {
        // O "campo" em si: ao tocar, abre o menu
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(selected, modifier = Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PrimaryBlue)
            }
        }
        // O menu com as opções (só aparece quando expanded = true)
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Janela de CONFIRMAÇÃO antes de excluir.
// Evita que o usuário apague algo sem querer.
@Composable
fun DeleteConfirmDialog(
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir") },
        text = { Text("Deseja realmente excluir \"$itemName\"?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Excluir", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = PrimaryBlue) }
        }
    )
}

// Mensagem mostrada quando a lista está VAZIA (ex.: "Nenhuma tarefa cadastrada").
@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Gray, textAlign = TextAlign.Center)
    }
}
