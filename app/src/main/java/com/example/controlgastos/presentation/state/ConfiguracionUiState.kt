package com.example.controlgastos.presentation.state

import com.example.controlgastos.domain.model.Usuario

data class ConfiguracionUiState(
    val perfil: Usuario? = null,
    val presupuestoMensual: Double = 500.0,
    val moneda: String = "S/",
    val modoOscuro: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)