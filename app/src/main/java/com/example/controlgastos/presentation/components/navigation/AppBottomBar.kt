package com.example.controlgastos.presentation.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.navigation.BottomNavItem

@Composable
fun AppBottomBar(
    rutaActual: String?,
    onItemClick: (BottomNavItem) -> Unit,
    onAgregarClick: () -> Unit
) {
    val itemsIzquierda = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.HistorialGastos
    )

    val itemsDerecha = listOf(
        BottomNavItem.Resumen,
        BottomNavItem.Configuracion
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.padding(top = 30.dp)
            //.offset(y = (-20).dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 26.dp,
                topEnd = 26.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 10.dp
        ) {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                itemsIzquierda.forEach { item ->
                    AppNavigationItem(
                        item = item,
                        seleccionado = rutaActual == item.ruta,
                        onClick = {
                            onItemClick(item)
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.size(
                        width = 76.dp,
                        height = 1.dp
                    )
                )

                itemsDerecha.forEach { item ->
                    AppNavigationItem(
                        item = item,
                        seleccionado = rutaActual == item.ruta,
                        onClick = {
                            onItemClick(item)
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-15).dp)
                .size(78.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
        )

        FloatingActionButton(
            onClick = onAgregarClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-5).dp)
                .size(64.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp,
                focusedElevation = 8.dp,
                hoveredElevation = 10.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Registrar gasto",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun RowScope.AppNavigationItem(
    item: BottomNavItem,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        modifier = Modifier.weight(1f),
        selected = seleccionado,
        onClick = onClick,
        alwaysShowLabel = true,
        icon = {
            Icon(
                imageVector = item.icono,
                contentDescription = item.titulo,
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Text(
                text = item.titulo,
                fontSize = 11.sp,
                fontWeight = if (seleccionado) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                },
                maxLines = 1
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor =
                MaterialTheme.colorScheme.primary,

            selectedTextColor =
                MaterialTheme.colorScheme.primary,

            indicatorColor =
                MaterialTheme.colorScheme.primaryContainer,

            unselectedIconColor =
                MaterialTheme.colorScheme.onSurfaceVariant,

            unselectedTextColor =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}