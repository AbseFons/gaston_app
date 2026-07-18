package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.repository.UsuarioRepository

class ObtenerPerfilUseCase(
    private val rep: UsuarioRepository
) {
    suspend operator fun invoke() = rep.obtenerPerfil()
}