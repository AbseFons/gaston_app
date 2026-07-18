package com.example.controlgastos.data.repository

import com.example.controlgastos.data.local.dao.UsuarioDao
import com.example.controlgastos.data.mapper.UsuarioMapper
import com.example.controlgastos.domain.model.Usuario
import com.example.controlgastos.domain.repository.UsuarioRepository

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao
) : UsuarioRepository {

    override suspend fun insertar(usuario: Usuario) {
        dao.agregar(UsuarioMapper.toEntity(usuario))
    }

    override suspend fun actualizar(usuario: Usuario) {
        dao.actualizar(UsuarioMapper.toEntity(usuario))
    }

    override suspend fun obtenerPerfil(): Usuario? {
        return dao.obtenerPerfil()?.let { UsuarioMapper.toDomain(it) }
    }
}