package com.example.controlgastos.presentation.state

import com.example.controlgastos.domain.model.Categoria
import com.example.controlgastos.domain.model.Gasto

data class GastoUiState(
    val gastos: List<Gasto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val totalGastado: Double = 0.0,
    val totalMes: Double = 0.0,
    val presupuestoMensual: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)