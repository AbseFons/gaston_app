package com.example.controlgastos.domain.usecase

data class GastoUseCase(
    val agregarGasto: AgregarGastoUseCase,
    val obtenerGastos: ObtenerGastosUseCase,
    val actualizarGasto: ActualizarGastoUseCase,
    val eliminarGasto: EliminarGastoUseCase,
    val obtenerTotalGastado: ObtenerTotalGastadoUseCase,
    val obtenerTotalPorMes: ObtenerTotalPorMesUseCase
)