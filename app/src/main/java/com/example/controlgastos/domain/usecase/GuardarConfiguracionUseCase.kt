package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.model.Configuracion
import com.example.controlgastos.domain.repository.ConfiguracionRepository

class GuardarConfiguracionUseCase(
    private val repository: ConfiguracionRepository
) {
    suspend operator fun invoke(
        configuracion: Configuracion
    ) {
        repository.guardarConfiguracion(configuracion)
    }
}