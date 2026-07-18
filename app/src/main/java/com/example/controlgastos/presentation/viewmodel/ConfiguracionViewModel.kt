package com.example.controlgastos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.domain.model.Configuracion
import com.example.controlgastos.domain.model.Usuario
import com.example.controlgastos.domain.usecase.ConfiguracionUseCase
import com.example.controlgastos.domain.usecase.UsuarioUseCase
import com.example.controlgastos.presentation.state.ConfiguracionUiEvent
import com.example.controlgastos.presentation.state.ConfiguracionUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfiguracionViewModel(
    private val usuarioUseCase: UsuarioUseCase,
    private val configuracionUseCase: ConfiguracionUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ConfiguracionUiState())

    val uiState: StateFlow<ConfiguracionUiState> =
        _uiState.asStateFlow()

    private val _event =
        MutableSharedFlow<ConfiguracionUiEvent>()

    val event =
        _event.asSharedFlow()

    init {
        cargarPerfil()
        observarConfiguracion()
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            try {
                val perfil =
                    usuarioUseCase.obtenerPerfil()

                _uiState.update {
                    it.copy(
                        perfil = perfil,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observarConfiguracion() {
        viewModelScope.launch {
            configuracionUseCase
                .obtenerConfiguracion()
                .collect { configuracion ->
                    _uiState.update {
                        it.copy(
                            presupuestoMensual =
                                configuracion.presupuestoMensual,
                            moneda =
                                configuracion.moneda,
                            modoOscuro =
                                configuracion.modoOscuro,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun guardar(
        nombres: String,
        apellidos: String,
        edad: Int,
        presupuesto: Double,
        moneda: String,
        modoOscuro: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true)
            }

            try {
                val usuario = Usuario(
                    id = _uiState.value.perfil?.id ?: 0,
                    nombres = nombres.trim(),
                    apellidos = apellidos.trim(),
                    edad = edad
                )

                usuarioUseCase.guardarPerfil(usuario)

                configuracionUseCase.guardarConfiguracion(
                    Configuracion(
                        presupuestoMensual = presupuesto,
                        moneda = moneda,
                        modoOscuro = modoOscuro
                    )
                )

                val perfilActualizado =
                    usuarioUseCase.obtenerPerfil()

                _uiState.update {
                    it.copy(
                        perfil = perfilActualizado,
                        isSaving = false,
                        error = null
                    )
                }

                _event.emit(
                    ConfiguracionUiEvent.Guardado
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message
                    )
                }

                _event.emit(
                    ConfiguracionUiEvent.MostrarSnackbar(
                        e.message
                            ?: "No se pudo guardar la configuración"
                    )
                )
            }
        }
    }
}