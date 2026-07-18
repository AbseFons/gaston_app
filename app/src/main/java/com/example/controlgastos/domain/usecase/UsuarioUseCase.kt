package com.example.controlgastos.domain.usecase

data class UsuarioUseCase(
    val guardarPerfil: GuardarPerfilUseCase,
    val obtenerPerfil: ObtenerPerfilUseCase
)