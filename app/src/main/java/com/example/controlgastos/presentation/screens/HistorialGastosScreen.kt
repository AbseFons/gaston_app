package com.example.controlgastos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.util.colorDesdeHex
import com.example.controlgastos.domain.model.Categoria
import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.presentation.components.GastoItemCard
import com.example.controlgastos.presentation.components.formatoMoneda
import com.example.controlgastos.presentation.components.header.AppTopBar
import com.example.controlgastos.presentation.viewmodel.GastoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialGastosScreen(
    vm: GastoViewModel
) {
    val uiState by vm.uiState.collectAsState()

    var busquedaActiva by rememberSaveable {
        mutableStateOf(false)
    }

    var gastoSeleccionado by remember {
        mutableStateOf<Gasto?>(null)
    }

    var textoBusqueda by rememberSaveable {
        mutableStateOf("")
    }

    var categoriaSeleccionadaId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val gastosFiltrados = remember(
        uiState.gastos,
        textoBusqueda,
        categoriaSeleccionadaId
    ) {
        val consulta = textoBusqueda.trim()

        uiState.gastos
            .filter { gasto ->
                consulta.isBlank() ||
                        gasto.descripcion.contains(
                            other = consulta,
                            ignoreCase = true
                        )
            }
            .filter { gasto ->
                categoriaSeleccionadaId == null ||
                        gasto.categoriaId == categoriaSeleccionadaId
            }
            .sortedWith(
                compareByDescending<Gasto> { it.fecha }
                    .thenByDescending { it.id }
            )
    }

    val categoriaSeleccionada = uiState.categorias.find {
        it.id == categoriaSeleccionadaId
    }

    val totalFiltrado = gastosFiltrados.sumOf {
        it.monto
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HistorialTopBar(
                busquedaActiva = busquedaActiva,
                textoBusqueda = textoBusqueda,
                onTextoBusquedaChange = {
                    textoBusqueda = it
                },
                onActivarBusqueda = {
                    busquedaActiva = true
                },
                onCerrarBusqueda = {
                    busquedaActiva = false
                    textoBusqueda = ""
                },
                categorias = uiState.categorias,
                categoriaSeleccionadaId = categoriaSeleccionadaId,
                onCategoriaSeleccionada = {
                    categoriaSeleccionadaId = it
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            uiState.error != null -> {
                ErrorHistorial(
                    mensaje = uiState.error
                        ?: "No se pudo cargar el historial",
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        ResumenHistorialCard(
                            cantidad = gastosFiltrados.size,
                            total = totalFiltrado
                        )
                    }

                    if (categoriaSeleccionada != null) {
                        item {
                            FiltroActivo(
                                nombreCategoria =
                                    categoriaSeleccionada.nombre,
                                onQuitarFiltro = {
                                    categoriaSeleccionadaId = null
                                }
                            )
                        }
                    }

                    if (gastosFiltrados.isEmpty()) {
                        item {
                            EmptyHistorialCard(
                                hayFiltros = textoBusqueda.isNotBlank() ||
                                        categoriaSeleccionadaId != null
                            )
                        }
                    } else {
                        items(
                            items = gastosFiltrados,
                            key = { gasto ->
                                gasto.id
                            }
                        ) { gasto ->
                            val categoria = uiState.categorias.find {
                                it.id == gasto.categoriaId
                            }

                            GastoItemCard(
                                descripcion = gasto.descripcion,
                                categoria = categoria?.nombre ?: "Sin categoría",
                                fecha = gasto.fecha.formatoFechaCorta(),
                                monto = gasto.monto,
                                colorCategoria = colorDesdeHex(categoria?.color)?: MaterialTheme.colorScheme.primary,
                                nota = gasto.nota,
                                mostrarNota = true,
                                onClick = {
                                    gastoSeleccionado = gasto
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    gastoSeleccionado?.let { gasto ->
        val categoria = uiState.categorias.find {
            it.id == gasto.categoriaId
        }

        DetalleGastoBottomSheet(
            gasto = gasto,
            nombreCategoria = categoria?.nombre ?: "Sin categoría",
            colorCategoria = colorDesdeHex(categoria?.color)?: MaterialTheme.colorScheme.primary,
            onDismiss = {
                gastoSeleccionado = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistorialTopBar(
    busquedaActiva: Boolean,
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    onActivarBusqueda: () -> Unit,
    onCerrarBusqueda: () -> Unit,
    categorias: List<Categoria>,
    categoriaSeleccionadaId: Int?,
    onCategoriaSeleccionada: (Int?) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var menuFiltroAbierto by remember {
        mutableStateOf(false)
    }

    AppTopBar(
        titulo = if (busquedaActiva) {
            ""
        } else {
            "Historial de gastos"
        },
        scrollBehavior = scrollBehavior,
        contenidoPersonalizado = if (busquedaActiva) {
            {
                CampoBusquedaHeader(
                    texto = textoBusqueda,
                    onTextoChange = onTextoBusquedaChange
                )
            }
        } else {
            null
        },
        actions = {
            if (!busquedaActiva) {
                IconButton(
                    onClick = onActivarBusqueda
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar gastos",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                IconButton(
                    onClick = onCerrarBusqueda
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar búsqueda",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box {
                IconButton(
                    onClick = {
                        menuFiltroAbierto = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar por categoría",
                        tint = if (
                            categoriaSeleccionadaId != null
                        ) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                if (categoriaSeleccionadaId != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(
                                x = (-8).dp,
                                y = 8.dp
                            )
                            .size(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                }

                DropdownMenu(
                    expanded = menuFiltroAbierto,
                    onDismissRequest = {
                        menuFiltroAbierto = false
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.widthIn(
                        min = 230.dp
                    )
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Todas las categorías",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onCategoriaSeleccionada(null)
                            menuFiltroAbierto = false
                        },
                        trailingIcon = {
                            if (categoriaSeleccionadaId == null) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline
                    )

                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = categoria.nombre,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = colorDesdeHex(categoria.color)?: MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                )
                            },
                            trailingIcon = {
                                if (
                                    categoriaSeleccionadaId ==
                                    categoria.id
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                onCategoriaSeleccionada(
                                    categoria.id
                                )
                                menuFiltroAbierto = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun CampoBusquedaHeader(
    texto: String,
    onTextoChange: (String) -> Unit
) {
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = texto,
        onValueChange = onTextoChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .focusRequester(focusRequester),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {}
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(
                        horizontal = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (texto.isEmpty()) {
                        Text(
                            text = "Buscar por descripción",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun ResumenHistorialCard(
    cantidad: Int,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Movimientos encontrados",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "$cantidad gastos",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Total",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "S/ ${total.formatoMoneda()}",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FiltroActivo(
    nombreCategoria: String,
    onQuitarFiltro: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(
                    start = 14.dp,
                    end = 6.dp,
                    top = 4.dp,
                    bottom = 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categoría: $nombreCategoria",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onQuitarFiltro,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quitar filtro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        TextButton(
            onClick = onQuitarFiltro
        ) {
            Text(
                text = "Limpiar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun EmptyHistorialCard(
    hayFiltros: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 34.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = if (hayFiltros) {
                    "No encontramos resultados"
                } else {
                    "Aún no hay gastos"
                },
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (hayFiltros) {
                    "Prueba con otra descripción o selecciona una categoría diferente."
                } else {
                    "Los gastos que registres aparecerán en esta sección."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ErrorHistorial(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No se pudo cargar el historial",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalleGastoBottomSheet(
    gasto: Gasto,
    nombreCategoria: String,
    colorCategoria: Color,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outline
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    bottom = 32.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle del gasto",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar detalle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = colorCategoria.copy(alpha = 0.14f)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = colorCategoria,
                                shape = CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = nombreCategoria,
                        color = colorCategoria,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = gasto.descripcion,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "- S/ ${gasto.monto.formatoMoneda()}",
                color = MaterialTheme.colorScheme.error,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            DetalleFila(
                etiqueta = "Fecha",
                valor = gasto.fecha.formatoFechaCorta()
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            DetalleFila(
                etiqueta = "Categoría",
                valor = nombreCategoria
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Nota",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(
                    text = gasto.nota.ifBlank {
                        "Este gasto no tiene una nota registrada."
                    },
                    color = if (gasto.nota.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DetalleFila(
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun String.formatoFechaCorta(): String {
    return try {
        val formatoEntrada = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val formatoSalida = SimpleDateFormat(
            "dd MMM yyyy",
            Locale("es", "PE")
        )

        formatoEntrada.parse(this)?.let {
            formatoSalida.format(it)
        } ?: this
    } catch (_: Exception) {
        this
    }
}