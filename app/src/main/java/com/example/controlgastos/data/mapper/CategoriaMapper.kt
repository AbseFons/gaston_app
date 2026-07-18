package com.example.controlgastos.data.mapper

import com.example.controlgastos.data.local.entity.CategoriaEntity
import com.example.controlgastos.domain.model.Categoria

object CategoriaMapper {
    fun toDomain(entidad: CategoriaEntity): Categoria =
        Categoria(entidad.id, entidad.nombre, entidad.color, entidad.icono)

    fun toEntity(categoria: Categoria): CategoriaEntity =
        CategoriaEntity(categoria.id, categoria.nombre, categoria.color, categoria.icono)
}