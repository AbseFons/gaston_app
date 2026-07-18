package com.example.controlgastos.domain.usecase

import com.example.controlgastos.domain.model.Usuario
import com.example.controlgastos.domain.repository.UsuarioRepository

class GuardarPerfilUseCase(
    private val rep: UsuarioRepository
) {
    suspend operator fun invoke(usuario: Usuario) {
        if (usuario.id == 0) {
            rep.insertar(usuario)
        } else {
            rep.actualizar(usuario)
        }
    }
}