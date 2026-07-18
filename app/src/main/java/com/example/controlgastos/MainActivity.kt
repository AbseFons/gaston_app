package com.example.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.controlgastos.core.navigation.AppNavigation
import com.example.controlgastos.core.ui.theme.AppSQLiteTheme
import com.example.controlgastos.di.AppModule

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        //installSplashScreen()

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

            AppSQLiteTheme(
                darkTheme =
                    configuracionState.modoOscuro,
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