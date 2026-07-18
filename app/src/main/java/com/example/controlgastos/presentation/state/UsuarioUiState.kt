package com.example.controlgastos.presentation.state

import com.example.controlgastos.domain.model.Usuario

data class UsuarioUiState(
    val perfil: Usuario? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)