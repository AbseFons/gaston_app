package com.example.controlgastos.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    data object Inicio : BottomNavItem(
        ruta = NavRutas.INICIO,
        titulo = "Inicio",
        icono = Icons.Default.Home
    )

    data object AgregarGasto : BottomNavItem(
        ruta = NavRutas.AGREGAR_GASTO,
        titulo = "Agregar",
        icono = Icons.Default.Add
    )

    data object HistorialGastos : BottomNavItem(
        ruta = NavRutas.HISTORIAL_GASTOS,
        titulo = "Historial",
        icono = Icons.Default.List
    )

    data object Resumen : BottomNavItem(
        ruta = NavRutas.RESUMEN,
        titulo = "Resumen",
        icono = Icons.Default.Info
    )

    data object Configuracion : BottomNavItem(
        ruta = NavRutas.CONFIGURACION,
        titulo = "Config.",
        icono = Icons.Default.Settings
    )
}