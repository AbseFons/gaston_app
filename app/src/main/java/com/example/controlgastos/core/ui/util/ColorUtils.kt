package com.example.controlgastos.core.ui.util

import androidx.compose.ui.graphics.Color

fun colorDesdeHex(
    hex: String?
): Color? {
    return try {
        if (hex.isNullOrBlank()) {
            null
        } else {
            Color(
                android.graphics.Color.parseColor(hex)
            )
        }
    } catch (_: Exception) {
        null
    }
}