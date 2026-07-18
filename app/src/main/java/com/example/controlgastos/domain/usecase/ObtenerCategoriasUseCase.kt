package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.repository.CategoriaRepository

class ObtenerCategoriasUseCase(
    private val repository: CategoriaRepository
) {
    suspend operator fun invoke() = repository.obtenerCategorias()
}