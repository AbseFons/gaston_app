package com.example.controlgastos.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.controlgastos.presentation.components.navigation.AppBottomBar
import com.example.controlgastos.presentation.screens.AgregarGastoScreen
import com.example.controlgastos.presentation.screens.ConfiguracionScreen
import com.example.controlgastos.presentation.screens.HistorialGastosScreen
import com.example.controlgastos.presentation.screens.InicioScreen
import com.example.controlgastos.presentation.screens.ResumenScreen
import com.example.controlgastos.presentation.viewmodel.ConfiguracionViewModel
import com.example.controlgastos.presentation.viewmodel.GastoViewModel
import com.example.controlgastos.presentation.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation(
    usuarioVm: UsuarioViewModel,
    gastoVm: GastoViewModel,
    configuracionVm: ConfiguracionViewModel
) {
    val navController = rememberNavController()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val rutaActual =
        backStackEntry?.destination?.route

    val rutasPrincipales = setOf(
        NavRutas.INICIO,
        NavRutas.HISTORIAL_GASTOS,
        NavRutas.RESUMEN,
        NavRutas.CONFIGURACION
    )

    val mostrarBarraInferior =
        rutaActual != null &&
                rutaActual in rutasPrincipales

    fun navegarPrincipal(
        ruta: String
    ) {
        navController.navigate(ruta) {
            launchSingleTop = true

            popUpTo(NavRutas.INICIO) {
                saveState = true
            }

            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (mostrarBarraInferior) {
                AppBottomBar(
                    rutaActual = rutaActual,
                    onItemClick = { item ->
                        navegarPrincipal(item.ruta)
                    },
                    onAgregarClick = {
                        navController.navigate(
                            NavRutas.AGREGAR_GASTO
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRutas.INICIO,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavRutas.INICIO) {
                InicioScreen(
                    usuarioVm = usuarioVm,
                    gastoVm = gastoVm,
                    configuracionVm = configuracionVm,
                    onConfiguracionClick = {
                        navegarPrincipal(
                            NavRutas.CONFIGURACION
                        )
                    }
                )
            }

            composable(NavRutas.AGREGAR_GASTO) {
                AgregarGastoScreen(
                    vm = gastoVm,
                    onBackClick = {
                        val pudoVolver =
                            navController.popBackStack()

                        if (!pudoVolver) {
                            navegarPrincipal(
                                NavRutas.INICIO
                            )
                        }
                    }
                )
            }

            composable(
                NavRutas.HISTORIAL_GASTOS
            ) {
                HistorialGastosScreen(
                    vm = gastoVm
                )
            }

            composable(NavRutas.RESUMEN) {
                ResumenScreen(
                    vm = gastoVm
                )
            }

            composable(
                NavRutas.CONFIGURACION
            ) {
                ConfiguracionScreen(
                    vm = configuracionVm,
                    onBackClick = {
                        val pudoVolver =
                            navController.popBackStack()

                        if (!pudoVolver) {
                            navegarPrincipal(
                                NavRutas.INICIO
                            )
                        }
                    },
                    onPerfilActualizado = {
                        usuarioVm.obtenerPerfil()
                    }
                )
            }
        }
    }
}