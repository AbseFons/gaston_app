package com.example.controlgastos.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.controlgastos.domain.model.Configuracion
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.configuracionDataStore by preferencesDataStore(
    name = "configuracion"
)

class ConfiguracionDataStore(
    private val context: Context
) {

    private companion object {
        val PRESUPUESTO_MENSUAL =
            doublePreferencesKey("presupuesto_mensual")

        val MONEDA =
            stringPreferencesKey("moneda")

        val MODO_OSCURO =
            booleanPreferencesKey("modo_oscuro")
    }

    val configuracion: Flow<Configuracion> =
        context.configuracionDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferencias ->
                Configuracion(
                    presupuestoMensual =
                        preferencias[PRESUPUESTO_MENSUAL] ?: 500.0,
                    moneda =
                        preferencias[MONEDA] ?: "S/",
                    modoOscuro =
                        preferencias[MODO_OSCURO] ?: false
                )
            }

    suspend fun guardar(
        configuracion: Configuracion
    ) {
        context.configuracionDataStore.edit { preferencias ->
            preferencias[PRESUPUESTO_MENSUAL] =
                configuracion.presupuestoMensual

            preferencias[MONEDA] =
                configuracion.moneda

            preferencias[MODO_OSCURO] =
                configuracion.modoOscuro
        }
    }
}