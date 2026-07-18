package com.example.controlgastos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.domain.model.Usuario
import com.example.controlgastos.domain.usecase.UsuarioUseCase
import com.example.controlgastos.presentation.state.UsuarioUiEvent
import com.example.controlgastos.presentation.state.UsuarioUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val useCase: UsuarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<UsuarioUiEvent>()
    val event = _event.asSharedFlow()

    init {
        obtenerPerfil()
    }

    fun obtenerPerfil() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val perfil = useCase.obtenerPerfil()
                _uiState.value = _uiState.value.copy(
                    perfil = perfil,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                _event.emit(UsuarioUiEvent.MostrarSnackbar(e.message ?: "Error al obtener perfil"))
            }
        }
    }

    fun guardarPerfil(usuario: Usuario) {
        viewModelScope.launch {
            try {
                useCase.guardarPerfil(usuario)
                obtenerPerfil()
                _event.emit(UsuarioUiEvent.MostrarSnackbar("Perfil guardado"))
                _event.emit(UsuarioUiEvent.NavigateBack)
            } catch (e: Exception) {
                _event.emit(UsuarioUiEvent.MostrarSnackbar(e.message ?: "Error al guardar perfil"))
            }
        }
    }
}