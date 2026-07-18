package com.example.controlgastos.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.controlgastos.core.ui.theme.BordeSuave
import com.example.controlgastos.core.ui.theme.Primario
import com.example.controlgastos.core.ui.theme.Tarjetas
import com.example.controlgastos.core.ui.theme.TextoPrincipal
import com.example.controlgastos.core.ui.theme.TextoSecundario

@Composable
fun CategoriaChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = {
            Text(
                text = texto,
                color = if (seleccionado) Color.White else TextoPrincipal
            )
        },
        modifier = modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(50.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Tarjetas,
            selectedContainerColor = Primario,
            labelColor = TextoSecundario,
            selectedLabelColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionado) Primario else BordeSuave
        )
    )
}