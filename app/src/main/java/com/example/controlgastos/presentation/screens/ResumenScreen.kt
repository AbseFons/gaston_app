package com.example.controlgastos.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.util.colorDesdeHex
import com.example.controlgastos.domain.model.Categoria
import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.presentation.components.formatoMoneda
import com.example.controlgastos.presentation.components.header.AppTopBar
import com.example.controlgastos.presentation.viewmodel.GastoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    vm: GastoViewModel
) {
    val uiState by vm.uiState.collectAsState()

    var mesSeleccionado by rememberSaveable {
        mutableStateOf(obtenerMesActual())
    }

    var mostrarSelectorMes by rememberSaveable {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val gastosMes = remember(
        uiState.gastos,
        mesSeleccionado
    ) {
        uiState.gastos
            .filter { gasto ->
                gasto.fecha.startsWith(mesSeleccionado)
            }
            .sortedWith(
                compareByDescending<Gasto> { it.fecha }
                    .thenByDescending { it.id }
            )
    }

    val totalMes = gastosMes.sumOf { it.monto }

    val promedioGasto = if (gastosMes.isNotEmpty()) {
        totalMes / gastosMes.size
    } else {
        0.0
    }

    val mayorGasto = gastosMes.maxByOrNull {
        it.monto
    }

    val resumenCategorias = remember(
        gastosMes,
        uiState.categorias
    ) {
        gastosMes
            .groupBy { gasto ->
                gasto.categoriaId
            }
            .mapNotNull { (categoriaId, gastosCategoria) ->
                val categoria = uiState.categorias.find {
                    it.id == categoriaId
                } ?: return@mapNotNull null

                val totalCategoria = gastosCategoria.sumOf {
                    it.monto
                }

                CategoriaResumen(
                    categoria = categoria,
                    total = totalCategoria,
                    porcentaje = if (totalMes > 0) {
                        (totalCategoria / totalMes).toFloat()
                    } else {
                        0f
                    }
                )
            }
            .sortedByDescending {
                it.total
            }
    }

    val periodosMes = remember(gastosMes) {
        obtenerGastosPorPeriodo(gastosMes)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                titulo = "Resumen",
                //subtitulo = mesSeleccionado.nombreMes(),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = {
                            mostrarSelectorMes = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Cambiar mes",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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
                ErrorResumen(
                    mensaje = uiState.error
                        ?: "No se pudo cargar el resumen",
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
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ResumenPrincipalCard(
                            mes = mesSeleccionado.nombreMes(),
                            total = totalMes,
                            cantidad = gastosMes.size,
                            promedio = promedioGasto
                        )
                    }

                    if (gastosMes.isEmpty()) {
                        item {
                            EmptyResumenCard(
                                mes = mesSeleccionado.nombreMes()
                            )
                        }
                    } else {
                        item {
                            DistribucionCategoriasCard(
                                categorias = resumenCategorias,
                                total = totalMes
                            )
                        }

                        item {
                            GastosPorPeriodoCard(
                                periodos = periodosMes
                            )
                        }

                        mayorGasto?.let { gasto ->
                            item {
                                val categoria = uiState.categorias.find {
                                    it.id == gasto.categoriaId
                                }

                                MayorGastoCard(
                                    gasto = gasto,
                                    nombreCategoria = categoria?.nombre
                                        ?: "Sin categoría",
                                    colorCategoria = colorDesdeHex(
                                        categoria?.color
                                    ) ?: MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarSelectorMes) {
        SelectorMesDialog(
            mesSeleccionado = mesSeleccionado,
            onDismiss = {
                mostrarSelectorMes = false
            },
            onMesSeleccionado = { nuevoMes ->
                mesSeleccionado = nuevoMes
                mostrarSelectorMes = false
            }
        )
    }
}

@Composable
private fun ResumenPrincipalCard(
    mes: String,
    total: Double,
    cantidad: Int,
    promedio: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Text(
                text = "Gasto total",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "S/ ${total.formatoMoneda()}",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = mes,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.25f)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricaResumen(
                    etiqueta = "Movimientos",
                    valor = cantidad.toString(),
                    modifier = Modifier.weight(1f)
                )

                MetricaResumen(
                    etiqueta = "Promedio",
                    valor = "S/ ${promedio.formatoMoneda()}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricaResumen(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = etiqueta,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = valor,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DistribucionCategoriasCard(
    categorias: List<CategoriaResumen>,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            TituloSeccion(
                titulo = "Distribución por categoría",
                subtitulo = "Participación de cada categoría"
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            DonutCategorias(
                categorias = categorias,
                total = total
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            categorias.forEachIndexed { index, resumen ->
                CategoriaResumenFila(
                    resumen = resumen
                )

                if (index < categorias.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = 11.dp
                        ),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutCategorias(
    categorias: List<CategoriaResumen>,
    total: Double
) {
    val colorBorde = MaterialTheme.colorScheme.outline
    val colorPrimario = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(156.dp)
        ) {
            val anchoTrazo = 20.dp.toPx()

            val areaArco = Size(
                width = size.width - anchoTrazo,
                height = size.height - anchoTrazo
            )

            val posicion = Offset(
                x = anchoTrazo / 2,
                y = anchoTrazo / 2
            )

            drawArc(
                color = colorBorde,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = posicion,
                size = areaArco,
                style = Stroke(
                    width = anchoTrazo,
                    cap = StrokeCap.Butt
                )
            )

            var anguloInicial = -90f

            categorias.forEach { resumen ->
                val barrido = resumen.porcentaje * 360f

                val colorCategoria =
                    colorDesdeHex(
                        resumen.categoria.color
                    ) ?: colorPrimario

                drawArc(
                    color = colorCategoria,
                    startAngle = anguloInicial,
                    sweepAngle = barrido,
                    useCenter = false,
                    topLeft = posicion,
                    size = areaArco,
                    style = Stroke(
                        width = anchoTrazo,
                        cap = StrokeCap.Butt
                    )
                )

                anguloInicial += barrido
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Text(
                text = "S/ ${total.formatoMoneda()}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategoriaResumenFila(
    resumen: CategoriaResumen
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = colorDesdeHex(
                        resumen.categoria.color
                    )?: MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )

        Spacer(
            modifier = Modifier.size(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = resumen.categoria.nombre,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${(resumen.porcentaje * 100).toInt()}% del total",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        Text(
            text = "S/ ${resumen.total.formatoMoneda()}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GastosPorPeriodoCard(
    periodos: List<PeriodoResumen>
) {
    val maximo = periodos.maxOfOrNull {
        it.total
    } ?: 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            TituloSeccion(
                titulo = "Evolución del mes",
                subtitulo = "Gasto acumulado por periodos de siete días"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                periodos.forEach { periodo ->
                    val proporcion = if (maximo > 0) {
                        (periodo.total / maximo).toFloat()
                    } else {
                        0f
                    }

                    BarraPeriodo(
                        periodo = periodo,
                        proporcion = proporcion,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraPeriodo(
    periodo: PeriodoResumen,
    proporcion: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (periodo.total > 0) {
                periodo.total.formatoCorto()
            } else {
                "-"
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            maxLines = 1
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val altura = if (periodo.total > 0) {
                (proporcion * 100f)
                    .coerceAtLeast(8f)
                    .dp
            } else {
                4.dp
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .height(altura)
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(
                        brush = if (periodo.total > 0) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.outline,
                                    MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = periodo.nombre,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun MayorGastoCard(
    gasto: Gasto,
    nombreCategoria: String,
    colorCategoria: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = colorCategoria.copy(
                            alpha = 0.15f
                        ),
                        shape = RoundedCornerShape(17.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .background(
                            color = colorCategoria,
                            shape = CircleShape
                        )
                )
            }

            Spacer(
                modifier = Modifier.size(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Mayor gasto del mes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Text(
                    text = gasto.descripcion,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = nombreCategoria,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "S/ ${gasto.monto.formatoMoneda()}",
                color = MaterialTheme.colorScheme.error,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TituloSeccion(
    titulo: String,
    subtitulo: String
) {
    Column {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = subtitulo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun EmptyResumenCard(
    mes: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 36.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "No hay gastos registrados",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "No se encontraron movimientos para $mes.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorResumen(
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
                    text = "No se pudo cargar el resumen",
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

@Composable
private fun SelectorMesDialog(
    mesSeleccionado: String,
    onDismiss: () -> Unit,
    onMesSeleccionado: (String) -> Unit
) {
    val partes = mesSeleccionado.split("-")

    val anioSeleccionado = partes
        .getOrNull(0)
        ?.toIntOrNull()
        ?: obtenerAnioActual()

    val numeroMesSeleccionado = partes
        .getOrNull(1)
        ?.toIntOrNull()
        ?: 1

    var anioVisible by remember {
        mutableStateOf(anioSeleccionado)
    }

    val nombresMeses = listOf(
        "Ene", "Feb", "Mar",
        "Abr", "May", "Jun",
        "Jul", "Ago", "Sep",
        "Oct", "Nov", "Dic"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Seleccionar mes",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            anioVisible--
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Año anterior",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = anioVisible.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            anioVisible++
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.KeyboardArrowRight,
                            contentDescription = "Año siguiente",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                nombresMeses
                    .chunked(3)
                    .forEachIndexed { fila, mesesFila ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            mesesFila.forEachIndexed { columna, nombre ->
                                val numeroMes =
                                    fila * 3 + columna + 1

                                val seleccionado =
                                    anioVisible == anioSeleccionado &&
                                            numeroMes ==
                                            numeroMesSeleccionado

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable {
                                            val nuevoMes =
                                                String.format(
                                                    Locale.US,
                                                    "%04d-%02d",
                                                    anioVisible,
                                                    numeroMes
                                                )

                                            onMesSeleccionado(
                                                nuevoMes
                                            )
                                        },
                                    shape = RoundedCornerShape(15.dp),
                                    color = if (seleccionado) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (seleccionado) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outline
                                        }
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment =
                                            Alignment.Center
                                    ) {
                                        Text(
                                            text = nombre,
                                            color = if (seleccionado) {
                                                Color.White
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                            fontSize = 14.sp,
                                            fontWeight =
                                                FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        if (fila < 3) {
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                        }
                    }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cerrar",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

private data class CategoriaResumen(
    val categoria: Categoria,
    val total: Double,
    val porcentaje: Float
)

private data class PeriodoResumen(
    val nombre: String,
    val total: Double
)

private fun obtenerGastosPorPeriodo(
    gastos: List<Gasto>
): List<PeriodoResumen> {
    val totales = DoubleArray(5)

    gastos.forEach { gasto ->
        val dia = gasto.fecha
            .split("-")
            .getOrNull(2)
            ?.toIntOrNull()
            ?: return@forEach

        val posicion = ((dia - 1) / 7)
            .coerceIn(0, 4)

        totales[posicion] += gasto.monto
    }

    val nombres = listOf(
        "1–7",
        "8–14",
        "15–21",
        "22–28",
        "29–fin"
    )

    return nombres.mapIndexed { index, nombre ->
        PeriodoResumen(
            nombre = nombre,
            total = totales[index]
        )
    }
}

private fun obtenerMesActual(): String {
    return SimpleDateFormat(
        "yyyy-MM",
        Locale.US
    ).format(Date())
}

private fun obtenerAnioActual(): Int {
    return SimpleDateFormat(
        "yyyy",
        Locale.US
    ).format(Date()).toInt()
}

private fun String.nombreMes(): String {
    return try {
        val entrada = SimpleDateFormat(
            "yyyy-MM",
            Locale.US
        )

        val salida = SimpleDateFormat(
            "MMMM yyyy",
            Locale("es", "PE")
        )

        entrada.parse(this)?.let { fecha ->
            salida.format(fecha).replaceFirstChar {
                it.uppercase()
            }
        } ?: this
    } catch (_: Exception) {
        this
    }
}

private fun Double.formatoCorto(): String {
    return when {
        this >= 1000 -> {
            "${(this / 1000).formatoMoneda()}k"
        }

        else -> {
            String.format(
                Locale.US,
                "%.0f",
                this
            )
        }
    }
}