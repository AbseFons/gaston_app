package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.repository.GastoRepository

class ObtenerTotalPorMesUseCase(
    private val repository: GastoRepository
) {
    suspend operator fun invoke(mes: String) = repository.obtenerTotalPorMes(mes)
}