package com.example.controlgastos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.domain.usecase.CategoriaUseCase
import com.example.controlgastos.domain.usecase.GastoUseCase
import com.example.controlgastos.presentation.state.GastoUiEvent
import com.example.controlgastos.presentation.state.GastoUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GastoViewModel(
    private val gastoUseCase: GastoUseCase,
    private val categoriaUseCase: CategoriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GastoUiState())
    val uiState: StateFlow<GastoUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<GastoUiEvent>()
    val event = _event.asSharedFlow()

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                categoriaUseCase.insertarIniciales()

                val gastos = gastoUseCase.obtenerGastos()
                val categorias = categoriaUseCase.obtenerCategorias()
                val total = gastoUseCase.obtenerTotalGastado()
                val totalMes = gastoUseCase.obtenerTotalPorMes(obtenerMesActual())

                _uiState.value = _uiState.value.copy(
                    gastos = gastos,
                    categorias = categorias,
                    totalGastado = total,
                    totalMes = totalMes,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                _event.emit(GastoUiEvent.MostrarSnackbar(e.message ?: "Error al cargar datos"))
            }
        }
    }

    fun agregarGasto(gasto: Gasto) {
        viewModelScope.launch {
            try {
                gastoUseCase.agregarGasto(gasto)
                cargarDatosIniciales()

                _event.emit(GastoUiEvent.NavigateBack)
            } catch (e: Exception) {
                _event.emit(
                    GastoUiEvent.MostrarSnackbar(
                        e.message ?: "Error al guardar gasto"
                    )
                )
            }
        }
    }

    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            try {
                gastoUseCase.eliminarGasto(gasto)
                cargarDatosIniciales()
                _event.emit(GastoUiEvent.MostrarSnackbar("Gasto eliminado"))
            } catch (e: Exception) {
                _event.emit(GastoUiEvent.MostrarSnackbar(e.message ?: "Error al eliminar gasto"))
            }
        }
    }

    private fun obtenerMesActual(): String {
        val formato = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return formato.format(Date())
    }
}