package com.example.controlgastos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.util.colorDesdeHex
import com.example.controlgastos.presentation.components.BalanceCard
import com.example.controlgastos.presentation.components.GastoItemCard
import com.example.controlgastos.presentation.components.formatoMoneda
import com.example.controlgastos.presentation.components.header.AppTopBar
import com.example.controlgastos.presentation.components.header.InicioHeader
import com.example.controlgastos.presentation.viewmodel.ConfiguracionViewModel
import com.example.controlgastos.presentation.viewmodel.GastoViewModel
import com.example.controlgastos.presentation.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    usuarioVm: UsuarioViewModel,
    gastoVm: GastoViewModel,
    configuracionVm: ConfiguracionViewModel,
    onConfiguracionClick: () -> Unit
) {
    val usuarioState by usuarioVm.uiState.collectAsState()
    val gastoState by gastoVm.uiState.collectAsState()
    val configuracionState by configuracionVm.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val nombre = usuarioState.perfil?.nombres ?: "Usuario"

    val presupuestoMensual = configuracionState.presupuestoMensual

    val moneda = configuracionState.moneda

    val gastadoMes = gastoState.totalMes
    val saldoDisponible = presupuestoMensual - gastadoMes

    val ultimosGastos = gastoState.gastos.take(5)

    val gastosMesActual = gastoState.gastos.filter { gasto ->
        gasto.fecha.startsWith(obtenerMesActual())
    }

    val categoriaMayorId = gastosMesActual
        .groupBy { it.categoriaId }
        .mapValues { entry ->
            entry.value.sumOf { gasto -> gasto.monto }
        }
        .maxByOrNull { entry ->
            entry.value
        }
        ?.key

    val nombreCategoriaMayor = categoriaMayorId?.let { id ->
        gastoState.categorias.find { categoria ->
            categoria.id == id
        }?.nombre
    } ?: "Sin datos"

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                scrollBehavior = scrollBehavior,
                contenidoPersonalizado = {
                    InicioHeader(
                        nombre = nombre,
                        onConfiguracionClick = onConfiguracionClick
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {
            if (gastoState.isLoading || configuracionState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                BalanceCard(
                    gastadoMes = gastadoMes,
                    presupuestoMensual = presupuestoMensual,
                    saldoDisponible = saldoDisponible,
                    moneda = moneda
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiniResumenCard(
                        titulo = "Disponible",
                        monto = "$moneda ${saldoDisponible.formatoMoneda()}",
                        color = MaterialTheme.colorScheme.tertiary,
                        fondo = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MiniResumenCard(
                        titulo = "Presupuesto",
                        monto = "$moneda ${presupuestoMensual.formatoMoneda()}",
                        color = MaterialTheme.colorScheme.primary,
                        fondo = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    titulo = "Últimos gastos",
                    accion = "${ultimosGastos.size} registros"
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (ultimosGastos.isEmpty()) {
                    EmptyGastosCard()
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ultimosGastos.forEach { gasto ->
                            val categoria = gastoState.categorias.find {
                                it.id == gasto.categoriaId
                            }

                            GastoItemCard(
                                descripcion = gasto.descripcion,
                                categoria = categoria?.nombre
                                    ?: "Sin categoría",
                                fecha = gasto.fecha.formatoFechaCorta(),
                                monto = gasto.monto,
                                colorCategoria = colorDesdeHex(
                                    categoria?.color
                                ) ?: MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ConsejoCard(
                    totalMes = gastadoMes,
                    categoriaMayor = nombreCategoriaMayor
                )
            }
        }
    }
}

@Composable
private fun MiniResumenCard(
    titulo: String,
    monto: String,
    color: Color,
    fondo: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fondo),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Text(
                text = monto,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionTitle(
    titulo: String,
    accion: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = accion,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyGastosCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Aún no hay gastos",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Agrega tu primer gasto para ver el resumen mensual.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ConsejoCard(
    totalMes: Double,
    categoriaMayor: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Consejo rápido",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tus gastos registrados este mes suman " +
                        "S/ ${totalMes.formatoMoneda()}. " +
                        "La categoría con mayor gasto es $categoriaMayor.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

private fun String.formatoFechaCorta(): String {
    return try {
        val entrada = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val salida = SimpleDateFormat(
            "dd MMM",
            Locale("es", "PE")
        )

        val fechaConvertida = entrada.parse(this)

        if (fechaConvertida != null) {
            salida.format(fechaConvertida)
        } else {
            this
        }
    } catch (_: Exception) {
        this
    }
}

private fun obtenerMesActual(): String {
    return SimpleDateFormat(
        "yyyy-MM",
        Locale.getDefault()
    ).format(System.currentTimeMillis())
}