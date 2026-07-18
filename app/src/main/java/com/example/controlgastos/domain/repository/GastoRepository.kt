package com.example.controlgastos.domain.repository

import com.example.controlgastos.domain.model.Gasto

interface GastoRepository {
    suspend fun insertar(gasto: Gasto)
    suspend fun actualizar(gasto: Gasto)
    suspend fun eliminar(gasto: Gasto)
    suspend fun obtenerGastos(): List<Gasto>
    suspend fun obtenerTotalGastado(): Double
    suspend fun obtenerTotalPorMes(mes: String): Double
}