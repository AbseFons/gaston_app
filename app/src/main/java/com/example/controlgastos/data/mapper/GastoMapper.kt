package com.example.controlgastos.data.mapper

import com.example.controlgastos.data.local.entity.GastoEntity
import com.example.controlgastos.domain.model.Gasto

object GastoMapper {
    fun toDomain(entidad: GastoEntity): Gasto =
        Gasto(
            id = entidad.id,
            descripcion = entidad.descripcion,
            monto = entidad.monto,
            fecha = entidad.fecha,
            categoriaId = entidad.categoriaId,
            usuarioId = entidad.usuarioId,
            nota = entidad.nota
        )

    fun toEntity(gasto: Gasto): GastoEntity =
        GastoEntity(
            id = gasto.id,
            descripcion = gasto.descripcion,
            monto = gasto.monto,
            fecha = gasto.fecha,
            categoriaId = gasto.categoriaId,
            usuarioId = gasto.usuarioId,
            nota = gasto.nota
        )
}