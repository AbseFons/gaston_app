package com.example.controlgastos.domain.model

data class Configuracion(
    val presupuestoMensual: Double = 500.0,
    val moneda: String = "S/",
    val modoOscuro: Boolean = false
)