package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.domain.repository.GastoRepository

class EliminarGastoUseCase(
    private val repository: GastoRepository
) {
    suspend operator fun invoke(gasto: Gasto) {
        repository.eliminar(gasto)
    }
}