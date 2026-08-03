package com.example.controlgastos.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlgastos.presentation.components.PrimaryButton
import com.example.controlgastos.presentation.components.header.AppTopBar
import com.example.controlgastos.presentation.state.ConfiguracionUiEvent
import com.example.controlgastos.presentation.viewmodel.ConfiguracionViewModel
import java.util.Locale

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ConfiguracionScreen(
    vm: ConfiguracionViewModel,
    onBackClick: () -> Unit,
    onPerfilActualizado: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()

    var nombres by rememberSaveable {
        mutableStateOf("")
    }

    var apellidos by rememberSaveable {
        mutableStateOf("")
    }

    var edad by rememberSaveable {
        mutableStateOf("")
    }

    var presupuesto by rememberSaveable {
        mutableStateOf("")
    }

    var moneda by rememberSaveable {
        mutableStateOf("S/")
    }

    var modoOscuro by rememberSaveable {
        mutableStateOf(false)
    }

    var nombresError by rememberSaveable {
        mutableStateOf(false)
    }

    var apellidosError by rememberSaveable {
        mutableStateOf(false)
    }

    var edadError by rememberSaveable {
        mutableStateOf(false)
    }

    var presupuestoError by rememberSaveable {
        mutableStateOf(false)
    }

    var formularioInicializado by rememberSaveable {
        mutableStateOf(false)
    }

    val snackbarHostState =
        remember { SnackbarHostState() }

    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(
            state = rememberTopAppBarState()
        )

    val coloresCampo =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error
        )

    LaunchedEffect(
        uiState.perfil,
        uiState.isLoading
    ) {
        if (
            !uiState.isLoading &&
            !formularioInicializado
        ) {
            nombres =
                uiState.perfil?.nombres.orEmpty()

            apellidos =
                uiState.perfil?.apellidos.orEmpty()

            edad =
                uiState.perfil?.edad
                    ?.toString()
                    .orEmpty()

            presupuesto =
                String.format(
                    Locale.US,
                    "%.2f",
                    uiState.presupuestoMensual
                )

            moneda = uiState.moneda
            modoOscuro = uiState.modoOscuro

            formularioInicializado = true
        }
    }

    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event) {
                is ConfiguracionUiEvent.MostrarSnackbar -> {
                    snackbarHostState.showSnackbar(
                        event.mensaje
                    )
                }

                ConfiguracionUiEvent.Guardado -> {
                    onPerfilActualizado()

                    snackbarHostState.showSnackbar(
                        "Configuración guardada"
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            AppTopBar(
                titulo = "Configuración",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
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
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            item {
                PerfilResumenCard(
                    nombres = nombres,
                    apellidos = apellidos
                )
            }

            item {
                CardConfiguracion(
                    titulo = "Perfil local",
                    subtitulo =
                        "Información del propietario de la aplicación"
                ) {
                    OutlinedTextField(
                        value = nombres,
                        onValueChange = {
                            nombres = it
                            nombresError = false
                        },
                        label = {
                            Text("Nombres")
                        },
                        isError = nombresError,
                        supportingText = {
                            if (nombresError) {
                                Text("Ingrese sus nombres")
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = {
                            apellidos = it
                            apellidosError = false
                        },
                        label = {
                            Text("Apellidos")
                        },
                        isError = apellidosError,
                        supportingText = {
                            if (apellidosError) {
                                Text("Ingrese sus apellidos")
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = edad,
                        onValueChange = {
                            if (
                                it.all(Char::isDigit) &&
                                it.length <= 3
                            ) {
                                edad = it
                                edadError = false
                            }
                        },
                        label = {
                            Text("Edad")
                        },
                        isError = edadError,
                        supportingText = {
                            if (edadError) {
                                Text("Ingrese una edad válida")
                            }
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampo,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                CardConfiguracion(
                    titulo = "Presupuesto",
                    subtitulo =
                        "Configura el límite mensual de gastos"
                ) {
                    OutlinedTextField(
                        value = presupuesto,
                        onValueChange = {
                            if (
                                it.isEmpty() ||
                                it.matches(
                                    Regex(
                                        "^\\d{0,8}([.,]\\d{0,2})?$"
                                    )
                                )
                            ) {
                                presupuesto = it
                                presupuestoError = false
                            }
                        },
                        label = {
                            Text("Presupuesto mensual")
                        },
                        prefix = {
                            Text("$moneda ")
                        },
                        isError = presupuestoError,
                        supportingText = {
                            if (presupuestoError) {
                                Text(
                                    "Ingrese un presupuesto mayor que cero"
                                )
                            }
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Decimal
                            ),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = coloresCampo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Moneda",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            "S/",
                            "$",
                            "€"
                        ).forEach { opcion ->
                            MonedaOption(
                                texto = opcion,
                                seleccionado =
                                    moneda == opcion,
                                onClick = {
                                    moneda = opcion
                                }
                            )
                        }
                    }
                }
            }

            item {
                CardConfiguracion(
                    titulo = "Apariencia"
//                    ,subtitulo = "Personaliza la visualización de la aplicación"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Modo oscuro",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(
                                text =
                                    "Utiliza colores oscuros en toda la aplicación",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        Switch(
                            checked = modoOscuro,
                            onCheckedChange = {
                                modoOscuro = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor =
                                    Color.White,
                                checkedTrackColor =
                                    MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor =
                                    MaterialTheme.colorScheme.primaryContainer,
                                uncheckedBorderColor =
                                    MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            item {
                PrimaryButton(
                    texto = if (uiState.isSaving) {
                        "Guardando..."
                    } else {
                        "Guardar cambios"
                    },
                    enabled = !uiState.isSaving,
                    onClick = {
                        val edadNumero =
                            edad.toIntOrNull()

                        val presupuestoNumero =
                            presupuesto
                                .replace(',', '.')
                                .toDoubleOrNull()

                        nombresError =
                            nombres.isBlank()

                        apellidosError =
                            apellidos.isBlank()

                        edadError =
                            edadNumero == null ||
                                    edadNumero <= 0

                        presupuestoError =
                            presupuestoNumero == null ||
                                    presupuestoNumero <= 0

                        if (
                            nombresError ||
                            apellidosError ||
                            edadError ||
                            presupuestoError
                        ) {
                            return@PrimaryButton
                        }

                        vm.guardar(
                            nombres = nombres,
                            apellidos = apellidos,
                            edad = edadNumero ?: 0,
                            presupuesto =
                                presupuestoNumero ?: 0.0,
                            moneda = moneda,
                            modoOscuro = modoOscuro
                        )
                    }
                )
            }

            item {
                AcercaDeCard()
            }
        }
    }
}

@Composable
private fun PerfilResumenCard(
    nombres: String,
    apellidos: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column {
                Text(
                    text = listOf(
                        nombres,
                        apellidos
                    )
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { "Usuario local" },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

//                Text(
//                    text = "Perfil almacenado en este dispositivo",
//                    color = TextoSecundario,
//                    fontSize = 13.sp
//                )
            }
        }
    }
}

@Composable
private fun CardConfiguracion(
    titulo: String? = null,
    subtitulo: String? = null,
    contenido: @Composable ColumnScope.() -> Unit
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
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column {
                if (titulo != null) {
                    Text(
                    text = titulo,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                    )
                }

                if (subtitulo != null) {
                    Text(
                    text = subtitulo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                    )
                }
            }

            contenido()
        }
    }
}

@Composable
private fun MonedaOption(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(
                width = 72.dp,
                height = 46.dp
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = if (seleccionado) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (seleccionado) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = if (seleccionado) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AcercaDeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = "Gaston",
//            color = TextoPrincipal,
//            fontSize = 15.sp,
//            fontWeight = FontWeight.Bold
//        )

        Text(
            text = "Gaston Versión 1.0",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Tus datos se almacenan localmente.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}