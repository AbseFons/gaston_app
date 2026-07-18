package com.example.controlgastos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.core.ui.theme.BordeSuave
import com.example.controlgastos.core.ui.theme.ErrorGasto
import com.example.controlgastos.core.ui.theme.Fondo
import com.example.controlgastos.core.ui.theme.FondoPrimarioSuave
import com.example.controlgastos.core.ui.theme.Primario
import com.example.controlgastos.core.ui.theme.Tarjetas
import com.example.controlgastos.core.ui.theme.TextoPrincipal
import com.example.controlgastos.core.ui.theme.TextoSecundario
import com.example.controlgastos.domain.model.Gasto
import com.example.controlgastos.presentation.components.CategoriaChip
import com.example.controlgastos.presentation.components.PrimaryButton
import com.example.controlgastos.presentation.components.header.AppTopBar
import com.example.controlgastos.presentation.state.GastoUiEvent
import com.example.controlgastos.presentation.viewmodel.GastoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AgregarGastoScreen(
    vm: GastoViewModel,
    onBackClick: () -> Unit
) {
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(fechaActual()) }
    var nota by remember { mutableStateOf("") }

    var montoError by remember { mutableStateOf(false) }
    var descripcionError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }

    var categoriaSeleccionadaId by remember {
        mutableStateOf<Int?>(null)
    }

    var mostrarSelectorFecha by remember {
        mutableStateOf(false)
    }

    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoPrincipal,
        unfocusedTextColor = TextoSecundario,
        errorTextColor = TextoPrincipal,
        focusedBorderColor = Primario,
        unfocusedBorderColor = BordeSuave,
        focusedLabelColor = Primario,
        unfocusedLabelColor = TextoSecundario,
        cursorColor = Primario,
        errorBorderColor = ErrorGasto,
        errorLabelColor = ErrorGasto,
        errorCursorColor = ErrorGasto
    )

    LaunchedEffect(uiState.categorias) {
        if (
            categoriaSeleccionadaId == null &&
            uiState.categorias.isNotEmpty()
        ) {
            categoriaSeleccionadaId = uiState.categorias.first().id
        }
    }

    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event) {
                is GastoUiEvent.MostrarSnackbar -> {
                    snackbarHostState.showSnackbar(event.mensaje)
                }

                GastoUiEvent.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        containerColor = Fondo,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            AppTopBar(
                titulo = "Registrar Gasto",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextoPrincipal
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Fondo)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            EncabezadoFormulario()

            MontoCard(
                monto = monto,
                montoError = montoError,
                onMontoChange = { nuevoMonto ->
                    if (nuevoMonto.esMontoPermitido()) {
                        monto = nuevoMonto
                        montoError = false
                    }
                },
                coloresCampo = coloresCampo,
                focusManager = focusManager
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Tarjetas
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Detalles del gasto",
                        color = TextoPrincipal,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            if (it.length <= 60) {
                                descripcion = it
                                descripcionError = false
                            }
                        },
                        label = {
                            Text("Descripción")
                        },
                        placeholder = {
                            Text("Ejemplo: Almuerzo")
                        },
                        singleLine = true,
                        isError = descripcionError,
                        supportingText = {
                            if (descripcionError) {
                                Text("Ingrese una descripción")
                            } else {
                                Text("${descripcion.length}/60")
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampo,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    CategoriaSection(
                        categorias = uiState.categorias.map {
                            it.id to it.nombre
                        },
                        categoriaSeleccionadaId =
                            categoriaSeleccionadaId,
                        onCategoriaSeleccionada = {
                            categoriaSeleccionadaId = it
                        }
                    )

                    Column {
                        Text(
                            text = "Fecha",
                            color = TextoPrincipal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = fecha,
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                isError = fechaError,
                                trailingIcon = {
                                    Icon(
                                        imageVector =
                                            Icons.Default.DateRange,
                                        contentDescription =
                                            "Seleccionar fecha",
                                        tint = Primario
                                    )
                                },
                                supportingText = {
                                    if (fechaError) {
                                        Text(
                                            "Seleccione una fecha válida"
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = coloresCampo,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        mostrarSelectorFecha = true
                                    }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = nota,
                        onValueChange = {
                            if (it.length <= 150) {
                                nota = it
                            }
                        },
                        label = {
                            Text("Nota opcional")
                        },
                        placeholder = {
                            Text("Agrega información adicional")
                        },
                        minLines = 3,
                        maxLines = 4,
                        supportingText = {
                            Text("${nota.length}/150")
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampo,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            PrimaryButton(
                texto = "Guardar gasto",
                enabled = !uiState.isLoading,
                onClick = {
                    focusManager.clearFocus()

                    val montoDouble = monto
                        .replace(',', '.')
                        .toDoubleOrNull()

                    montoError =
                        montoDouble == null || montoDouble <= 0

                    descripcionError = descripcion.isBlank()

                    fechaError = fecha.isBlank()

                    val categoriaId = categoriaSeleccionadaId

                    if (
                        montoError ||
                        descripcionError ||
                        fechaError ||
                        categoriaId == null
                    ) {
                        return@PrimaryButton
                    }

                    val gasto = Gasto(
                        descripcion = descripcion.trim(),
                        monto = montoDouble ?: 0.0,
                        fecha = fecha,
                        categoriaId = categoriaId,
                        usuarioId = 1,
                        nota = nota.trim()
                    )

                    vm.agregarGasto(gasto)
                }
            )

            Text(
                text = "El gasto se guardará únicamente en este dispositivo.",
                color = TextoSecundario,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (mostrarSelectorFecha) {
        SelectorFechaDialog(
            onDismiss = {
                mostrarSelectorFecha = false
            },
            onFechaSeleccionada = {
                fecha = it
                fechaError = false
                mostrarSelectorFecha = false
            }
        )
    }
}

@Composable
private fun EncabezadoFormulario() {
    Column {
        Text(
            text = "Añade un nuevo movimiento",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Completa la información para mantener actualizado tu presupuesto.",
            color = TextoSecundario,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun MontoCard(
    monto: String,
    montoError: Boolean,
    onMontoChange: (String) -> Unit,
    coloresCampo: androidx.compose.material3.TextFieldColors,
    focusManager: FocusManager
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoPrimarioSuave
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(start = 18.dp, top = 12.dp, end = 18.dp, bottom = 6.dp)
        ) {
            Text(
                text = "Monto del gasto",
                color = TextoSecundario,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = monto,
                onValueChange = onMontoChange,
                prefix = {
                    Text(
                        text = "S/ ",
                        color = Primario,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                placeholder = {
                    Text(
                        text = "0.00",
                        fontSize = 26.sp,
                        color = TextoSecundario
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextoPrincipal,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                isError = montoError,
                supportingText = {
                    if (montoError) {
                        Text("Ingrese un monto mayor que cero")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }
                ),
                shape = RoundedCornerShape(18.dp),
                colors = coloresCampo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun CategoriaSection(
    categorias: List<Pair<Int, String>>,
    categoriaSeleccionadaId: Int?,
    onCategoriaSeleccionada: (Int) -> Unit
) {
    Column {
        Text(
            text = "Categoría",
            color = TextoPrincipal,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (categorias.isEmpty()) {
            Text(
                text = "Cargando categorías...",
                color = TextoSecundario,
                fontSize = 13.sp
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    CategoriaChip(
                        texto = categoria.second,
                        seleccionado =
                            categoriaSeleccionadaId == categoria.first,
                        onClick = {
                            onCategoriaSeleccionada(
                                categoria.first
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorFechaDialog(
    onDismiss: () -> Unit,
    onFechaSeleccionada: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onFechaSeleccionada(
                            it.formatoFechaBaseDatos()
                        )
                    }
                }
            ) {
                Text(
                    text = "Aceptar",
                    color = Primario,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancelar",
                    color = TextoSecundario
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

private fun String.esMontoPermitido(): Boolean {
    return isEmpty() ||
            matches(
                Regex("^\\d{0,7}([.,]\\d{0,2})?$")
            )
}

private fun Long.formatoFechaBaseDatos(): String {
    val formato = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    formato.timeZone = TimeZone.getTimeZone("UTC")

    return formato.format(Date(this))
}

private fun fechaActual(): String {
    return SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(Date())
}