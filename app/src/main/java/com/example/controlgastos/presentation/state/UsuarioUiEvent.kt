package com.example.controlgastos.presentation.state

sealed class UsuarioUiEvent {
    data class MostrarSnackbar(val mensaje: String): UsuarioUiEvent()
    data object NavigateBack : UsuarioUiEvent()
}