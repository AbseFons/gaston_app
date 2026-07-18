package com.example.controlgastos.domain.model

data class Gasto(
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val fecha: String,
    val categoriaId: Int,
    val usuarioId: Int,
    val nota: String = ""
)