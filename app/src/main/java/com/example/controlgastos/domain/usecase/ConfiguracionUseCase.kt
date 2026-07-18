package com.example.controlgastos.domain.usecase

data class ConfiguracionUseCase(
    val obtenerConfiguracion: ObtenerConfiguracionUseCase,
    val guardarConfiguracion: GuardarConfiguracionUseCase
)