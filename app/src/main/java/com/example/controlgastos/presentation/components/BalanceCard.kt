package com.example.controlgastos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.theme.Acento
import com.example.controlgastos.core.ui.theme.Primario
import com.example.controlgastos.core.ui.theme.Tarjetas

@Composable
fun BalanceCard(
    gastadoMes: Double,
    presupuestoMensual: Double,
    saldoDisponible: Double,
    moneda: String,
    modifier: Modifier = Modifier
) {
    val progreso = if (presupuestoMensual > 0) {
        (gastadoMes / presupuestoMensual).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Tarjetas),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primario, Acento)
                    )
                )
                .padding(22.dp)
        ) {
            Text(
                text = "Gastado este mes",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )

            Text(
                text = "$moneda ${gastadoMes.formatoMoneda()}",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Presupuesto: S/ ${presupuestoMensual.formatoMoneda()}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )

                Text(
                    text = "Saldo: S/ ${saldoDisponible.formatoMoneda()}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(9.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progreso)
                        .height(9.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

fun Double.formatoMoneda(): String {
    return String.format("%.2f", this)
}