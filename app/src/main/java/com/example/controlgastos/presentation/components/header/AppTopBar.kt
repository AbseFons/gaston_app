package com.example.controlgastos.presentation.components.header

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.theme.Fondo
import com.example.controlgastos.core.ui.theme.TextoPrincipal
import com.example.controlgastos.core.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    titulo: String = "",
    subtitulo: String? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    contenidoPersonalizado: (@Composable () -> Unit)? = null
) {
    val contenidoSuperpuesto =
        (scrollBehavior?.state?.overlappedFraction ?: 0f) > 0.01f

    val elevacion by animateDpAsState(
        targetValue = if (contenidoSuperpuesto) 4.dp else 0.dp,
        label = "app_top_bar_elevation"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Fondo,
        tonalElevation = 0.dp,
        shadowElevation = elevacion
    ) {
        TopAppBar(
            title = {
                if (contenidoPersonalizado != null) {
                    contenidoPersonalizado()
                } else {
                    Column {
                        Text(
                            text = titulo,
                            color = TextoPrincipal,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (!subtitulo.isNullOrBlank()) {
                            Text(
                                text = subtitulo,
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets(top = 3.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Fondo,
                scrolledContainerColor = Fondo,
                navigationIconContentColor = TextoPrincipal,
                titleContentColor = TextoPrincipal,
                actionIconContentColor = TextoPrincipal
            )
        )
    }
}