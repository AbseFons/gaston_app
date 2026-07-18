package com.example.controlgastos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.theme.ErrorGasto
import com.example.controlgastos.core.ui.theme.Tarjetas
import com.example.controlgastos.core.ui.theme.TextoPrincipal
import com.example.controlgastos.core.ui.theme.TextoSecundario

@Composable
fun GastoItemCard(
    descripcion: String,
    categoria: String,
    fecha: String,
    monto: Double,
    colorCategoria: Color,
    modifier: Modifier = Modifier,
    nota: String? = null,
    mostrarNota: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val modifierClick = if (onClick != null) {
        Modifier.clickable {
            onClick()
        }
    } else {
        Modifier
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(modifierClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Tarjetas
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        colorCategoria.copy(alpha = 0.14f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(colorCategoria)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = descripcion,
                    color = TextoPrincipal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$categoria · $fecha",
                    color = TextoSecundario,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (
                    mostrarNota &&
                    !nota.isNullOrBlank()
                ) {
                    Text(
                        text = nota,
                        color = TextoSecundario,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "- S/ ${monto.formatoMoneda()}",
                color = ErrorGasto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}