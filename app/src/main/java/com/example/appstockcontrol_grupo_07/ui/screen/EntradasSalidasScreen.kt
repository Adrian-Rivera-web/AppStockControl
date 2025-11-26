package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.viewmodel.MovimientoInventarioViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.appstockcontrol_grupo_07.model.MovimientoInventario
import com.example.appstockcontrol_grupo_07.model.TipoMovimiento
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalContext

// Imports de recurso nativo de vibracion
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EntradasSalidasScreen(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    movimientosViewModel: MovimientoInventarioViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val productos by productoViewModel.productos.collectAsState()
    val movimientosState by movimientosViewModel.uiState.collectAsState()
    val usuarioActual by usuarioViewModel.usuarioLogueado.collectAsState()

    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var cantidadTexto by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    // 🔄 Estados para animación y mensaje de éxito
    var cargandoMovimiento by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔔 Función local para vibrar cuando la validación falle
    fun vibrarError() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    150L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150L)
        }
    }

    LaunchedEffect(productos) {
        if (productoSeleccionado == null && productos.isNotEmpty()) {
            productoSeleccionado = productos.first()
            movimientosViewModel.cargarKardexProducto(productos.first().id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔙 FILA CON BOTÓN ATRÁS + TÍTULO
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = "Movimientos de inventario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "Productos cargados: ${productos.size}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))

        // Selector de producto
        ProductoSelector(
            productos = productos,
            productoSeleccionado = productoSeleccionado,
            onSeleccionar = { prod ->
                productoSeleccionado = prod
                movimientosViewModel.cargarKardexProducto(prod.id)
            }
        )

        Spacer(Modifier.height(16.dp))

        // Formulario de entrada/salida
        OutlinedTextField(
            value = cantidadTexto,
            onValueChange = { if (it.all { c -> c.isDigit() }) cantidadTexto = it },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            label = { Text("Motivo (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 👉 ENTRADA con animación + éxito + vibración en error
            Button(
                onClick = {
                    val prod = productoSeleccionado
                    val cant = cantidadTexto.toIntOrNull()
                    val user = usuarioActual ?: "desconocido"

                    if (prod != null && cant != null && cant > 0) {
                        cargandoMovimiento = true
                        mensajeExito = null

                        movimientosViewModel.registrarEntrada(
                            producto = prod,
                            cantidad = cant,
                            usuario = user,
                            motivo = motivo
                        )
                        cantidadTexto = ""
                        motivo = ""

                        scope.launch {
                            delay(800L) // pequeña animación de carga
                            cargandoMovimiento = false
                            mensajeExito = "Entrada de producto registrada con éxito ✅"
                            delay(1500L) // mostrar mensaje un rato
                            mensajeExito = null
                        }
                    } else {
                        vibrarError()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Registrar ENTRADA")
            }

            // 👉 SALIDA con validación de stock + animación + éxito + vibración en error
            Button(
                onClick = {
                    val prod = productoSeleccionado
                    val cant = cantidadTexto.toIntOrNull()
                    val user = usuarioActual ?: "desconocido"

                    if (prod != null && cant != null && cant > 0) {
                        // ⛔ no permitir sacar más que el stock disponible
                        if (cant <= prod.stock) {
                            cargandoMovimiento = true
                            mensajeExito = null

                            movimientosViewModel.registrarSalida(
                                producto = prod,
                                cantidad = cant,
                                usuario = user,
                                motivo = motivo
                            )
                            cantidadTexto = ""
                            motivo = ""

                            scope.launch {
                                delay(800L)
                                cargandoMovimiento = false
                                mensajeExito = "Salida de producto registrada con éxito ✅"
                                delay(1500L)
                                mensajeExito = null
                            }
                        } else {
                            vibrarError()
                            // podrías también mostrar un mensaje en pantalla si quieres
                        }
                    } else {
                        vibrarError()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Registrar SALIDA")
            }
        }

        // 🔄 Animación de carga del movimiento
        if (cargandoMovimiento) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Registrando movimiento...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // ✅ Mensaje de éxito
        mensajeExito?.let { mensaje ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Éxito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Kárdex del producto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        if (movimientosState.cargando) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(movimientosState.movimientos) { mov ->
                    MovimientoItem(movimiento = mov)
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSelector(
    productos: List<Producto>,
    productoSeleccionado: Producto?,
    onSeleccionar: (Producto) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Producto",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = productoSeleccionado?.nombre ?: "Selecciona un producto",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Desplegar lista de productos"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors()
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                productos.forEach { prod ->
                    val lowStock = prod.stock <= prod.stockMinimo
                    val stockColor = if (lowStock) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = prod.nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = prod.categoria,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Stock: ${prod.stock}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = stockColor
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSeleccionar(prod)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MovimientoItem(movimiento: MovimientoInventario) {
    val esEntrada = movimiento.tipo == TipoMovimiento.ENTRADA
    val colorTipo = if (esEntrada) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    // 🕒 Formatear la fecha (usa el Long fecha = System.currentTimeMillis())
    val fechaFormateada = remember(movimiento.fecha) {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formato.format(Date(movimiento.fecha))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Línea principal: tipo + cantidad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (esEntrada) "ENTRADA" else "SALIDA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colorTipo
            )
            Text(
                text = "Cant: ${movimiento.cantidad}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 🕒 Fecha
        Text(
            text = "Fecha: $fechaFormateada",
            style = MaterialTheme.typography.bodySmall
        )

        // Usuario
        Text(
            text = "Usuario: ${movimiento.usuario}",
            style = MaterialTheme.typography.bodySmall
        )

        // Stock antes y después
        Text(
            text = "Stock: ${movimiento.stockAnterior} → ${movimiento.stockNuevo}",
            style = MaterialTheme.typography.bodySmall
        )

        // Motivo (si no está vacío)
        if (movimiento.motivo.isNotBlank()) {
            Text(
                text = "Motivo: ${movimiento.motivo}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
