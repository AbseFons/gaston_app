package com.example.controlgastos.data.repository

import com.example.controlgastos.data.local.preferences.ConfiguracionDataStore
import com.example.controlgastos.domain.model.Configuracion
import com.example.controlgastos.domain.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.Flow

class ConfiguracionRepositoryImpl(
    private val dataStore: ConfiguracionDataStore
) : ConfiguracionRepository {

    override fun obtenerConfiguracion(): Flow<Configuracion> {
        return dataStore.configuracion
    }

    override suspend fun guardarConfiguracion(
        configuracion: Configuracion
    ) {
        dataStore.guardar(configuracion)
    }
}