package com.example.controlgastos.domain.repository

import com.example.controlgastos.domain.model.Usuario

interface UsuarioRepository {
    suspend fun insertar(usuario: Usuario)
    suspend fun actualizar(usuario: Usuario)
    suspend fun obtenerPerfil(): Usuario?
}