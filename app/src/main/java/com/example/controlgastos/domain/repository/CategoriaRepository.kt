package com.example.controlgastos.domain.repository

import com.example.controlgastos.domain.model.Categoria

interface CategoriaRepository {
    suspend fun obtenerCategorias(): List<Categoria>
    suspend fun insertarCategoriasIniciales()
}