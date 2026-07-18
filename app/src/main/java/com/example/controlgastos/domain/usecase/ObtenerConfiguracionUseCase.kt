package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.repository.ConfiguracionRepository

class ObtenerConfiguracionUseCase(
    private val repository: ConfiguracionRepository
) {
    operator fun invoke() =
        repository.obtenerConfiguracion()
}