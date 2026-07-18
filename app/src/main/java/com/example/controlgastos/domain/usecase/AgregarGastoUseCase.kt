package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.domain.repository.GastoRepository

class AgregarGastoUseCase(
    private val repository: GastoRepository
) {
    suspend operator fun invoke(gasto: Gasto) {
        repository.insertar(gasto)
    }
}