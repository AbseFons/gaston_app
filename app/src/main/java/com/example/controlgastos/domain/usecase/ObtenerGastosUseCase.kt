package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.repository.GastoRepository

class ObtenerGastosUseCase(
    private val repository: GastoRepository
) {
    suspend operator fun invoke() = repository.obtenerGastos()
}