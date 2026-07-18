package com.example.controlgastos.domain.usecase

data class CategoriaUseCase(
    val insertarIniciales: InsertarCategoriasInicialesUseCase,
    val obtenerCategorias: ObtenerCategoriasUseCase
)