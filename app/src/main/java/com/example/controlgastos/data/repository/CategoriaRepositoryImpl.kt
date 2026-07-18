package com.example.controlgastos.data.repository

import com.example.controlgastos.data.local.dao.CategoriaDao
import com.example.controlgastos.data.local.entity.CategoriaEntity
import com.example.controlgastos.data.mapper.CategoriaMapper
import com.example.controlgastos.domain.model.Categoria
import com.example.controlgastos.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val dao: CategoriaDao
) : CategoriaRepository {

    override suspend fun obtenerCategorias(): List<Categoria> {
        return dao.obtenerCategorias().map { CategoriaMapper.toDomain(it) }
    }

    override suspend fun insertarCategoriasIniciales() {
        if (dao.contarCategorias() == 0) {
            dao.insertarTodas(
                listOf(
                    CategoriaEntity(nombre = "Comida", color = "#FF7043", icono = "restaurant"),
                    CategoriaEntity(nombre = "Transporte", color = "#42A5F5", icono = "directions_bus"),
                    CategoriaEntity(nombre = "Estudios", color = "#AB47BC", icono = "school"),
                    CategoriaEntity(nombre = "Salud", color = "#66BB6A", icono = "health"),
                    CategoriaEntity(nombre = "Servicios", color = "#FFA726", icono = "receipt"),
                    CategoriaEntity(nombre = "Otros", color = "#78909C", icono = "more")
                )
            )
        }
    }
}