package com.example.mypersonalorganizerv2.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.mypersonalorganizerv2.ui.theme.PrimaryBlue

@Composable
fun PriorityOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
        )
        Text(label)
    }
}
