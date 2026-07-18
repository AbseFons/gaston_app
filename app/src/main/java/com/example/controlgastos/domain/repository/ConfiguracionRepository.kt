package com.example.controlgastos.domain.repository

import com.example.controlgastos.domain.model.Configuracion
import kotlinx.coroutines.flow.Flow

interface ConfiguracionRepository {

    fun obtenerConfiguracion(): Flow<Configuracion>

    suspend fun guardarConfiguracion(
        configuracion: Configuracion
    )
}