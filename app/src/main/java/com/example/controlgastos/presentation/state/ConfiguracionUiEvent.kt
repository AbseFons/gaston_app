package com.example.controlgastos.presentation.state

sealed class ConfiguracionUiEvent {

    data class MostrarSnackbar(
        val mensaje: String
    ) : ConfiguracionUiEvent()

    data object Guardado : ConfiguracionUiEvent()
}