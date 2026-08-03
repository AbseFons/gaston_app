package com.example.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.controlgastos.core.navigation.AppNavigation
import com.example.controlgastos.core.ui.theme.AppSQLiteTheme
import com.example.controlgastos.di.AppModule

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        val usuarioViewModel =
            AppModule.provideUsuarioViewModel(
                applicationContext
            )

        val gastoViewModel =
            AppModule.provideGastoViewModel(
                applicationContext
            )

        val configuracionViewModel =
            AppModule.provideConfiguracionViewModel(
                applicationContext
            )

        enableEdgeToEdge()

        setContent {

            val configuracionState by
            configuracionViewModel
                .uiState
                .collectAsState()

            val modoOscuro =
                configuracionState.modoOscuro

            val view = LocalView.current

            SideEffect {
                val controller =
                    WindowCompat.getInsetsController(
                        window,
                        view
                    )

                controller
                    .isAppearanceLightStatusBars =
                    !modoOscuro

                controller
                    .isAppearanceLightNavigationBars =
                    !modoOscuro
            }

            AppSQLiteTheme(
                darkTheme = modoOscuro,
                dynamicColor = false
            ) {
                AppNavigation(
                    usuarioVm = usuarioViewModel,
                    gastoVm = gastoViewModel,
                    configuracionVm =
                        configuracionViewModel
                )
            }
        }
    }
}