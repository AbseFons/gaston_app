package com.example.controlgastos.data.repository

import com.example.controlgastos.data.local.dao.GastoDao
import com.example.controlgastos.data.mapper.GastoMapper
import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.domain.repository.GastoRepository

class GastoRepositoryImpl(
    private val dao: GastoDao
) : GastoRepository {

    override suspend fun insertar(gasto: Gasto) {
        dao.insertar(GastoMapper.toEntity(gasto))
    }

    override suspend fun actualizar(gasto: Gasto) {
        dao.actualizar(GastoMapper.toEntity(gasto))
    }

    override suspend fun eliminar(gasto: Gasto) {
        dao.eliminar(GastoMapper.toEntity(gasto))
    }

    override suspend fun obtenerGastos(): List<Gasto> {
        return dao.obtenerGastos().map { GastoMapper.toDomain(it) }
    }

    override suspend fun obtenerTotalGastado(): Double {
        return dao.obtenerTotalGastado() ?: 0.0
    }

    override suspend fun obtenerTotalPorMes(mes: String): Double {
        return dao.obtenerTotalPorMes(mes) ?: 0.0
    }
}