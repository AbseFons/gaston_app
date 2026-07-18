package com.example.controlgastos.data.mapper

import com.example.controlgastos.data.local.entity.UsuarioEntity
import com.example.controlgastos.domain.model.Usuario

object UsuarioMapper {
    fun toDomain(entidad: UsuarioEntity) : Usuario =
        Usuario(entidad.id,entidad.nombres,entidad.apellidos,entidad.edad)

    fun toEntity(usuario: Usuario ) : UsuarioEntity =
        UsuarioEntity(usuario.id,usuario.nombres,usuario.apellidos,usuario.edad)
}