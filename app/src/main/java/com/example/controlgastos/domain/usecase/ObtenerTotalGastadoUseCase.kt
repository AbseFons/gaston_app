package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.repository.GastoRepository

class ObtenerTotalGastadoUseCase(
    private val repository: GastoRepository
) {
    suspend operator fun invoke() = repository.obtenerTotalGastado()
}