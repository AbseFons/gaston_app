package com.example.controlgastos.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val GastonDarkColorScheme = darkColorScheme(
    primary = GastonDarkPrimary,
    onPrimary = GastonDarkOnPrimary,
    primaryContainer = GastonDarkPrimaryContainer,
    onPrimaryContainer = GastonDarkOnPrimaryContainer,

    secondary = GastonDarkSecondary,
    onSecondary = GastonDarkOnSecondary,
    secondaryContainer = GastonDarkSecondaryContainer,
    onSecondaryContainer = GastonDarkOnSecondaryContainer,

    tertiary = GastonDarkTertiary,
    onTertiary = GastonDarkOnTertiary,
    tertiaryContainer = GastonDarkTertiaryContainer,
    onTertiaryContainer = GastonDarkOnTertiaryContainer,

    background = GastonDarkBackground,
    onBackground = GastonDarkOnBackground,

    surface = GastonDarkSurface,
    onSurface = GastonDarkOnSurface,

    surfaceVariant = GastonDarkSurfaceVariant,
    onSurfaceVariant = GastonDarkOnSurfaceVariant,

    outline = GastonDarkOutline,

    error = GastonDarkError,
    onError = GastonDarkOnError,
    errorContainer = GastonDarkErrorContainer,
    onErrorContainer = GastonDarkOnErrorContainer
)

private val GastonLightColorScheme = lightColorScheme(
    primary = GastonLightPrimary,
    onPrimary = GastonLightOnPrimary,
    primaryContainer = GastonLightPrimaryContainer,
    onPrimaryContainer = GastonLightOnPrimaryContainer,

    secondary = GastonLightSecondary,
    onSecondary = GastonLightOnSecondary,
    secondaryContainer = GastonLightSecondaryContainer,
    onSecondaryContainer = GastonLightOnSecondaryContainer,

    tertiary = GastonLightTertiary,
    onTertiary = GastonLightOnTertiary,
    tertiaryContainer = GastonLightTertiaryContainer,
    onTertiaryContainer = GastonLightOnTertiaryContainer,

    background = GastonLightBackground,
    onBackground = GastonLightOnBackground,

    surface = GastonLightSurface,
    onSurface = GastonLightOnSurface,

    surfaceVariant = GastonLightSurfaceVariant,
    onSurfaceVariant = GastonLightOnSurfaceVariant,

    outline = GastonLightOutline,

    error = GastonLightError,
    onError = GastonLightOnError,
    errorContainer = GastonLightErrorContainer,
    onErrorContainer = GastonLightOnErrorContainer
)

@Composable
fun AppSQLiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> GastonDarkColorScheme
        else -> GastonLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}