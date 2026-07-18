package com.example.controlgastos.presentation.state

sealed class GastoUiEvent {
    data class MostrarSnackbar(val mensaje: String): GastoUiEvent()
    data object NavigateBack : GastoUiEvent()
}