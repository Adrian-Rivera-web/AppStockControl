package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.navigation.Route
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoId: String?
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val productoRepository = remember { ProductoRepository(database.productoDao()) }

    var producto by remember { mutableStateOf<Producto?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(productoId) {
        val idInt = productoId?.toIntOrNull()
        if (idInt != null) {
            producto = productoRepository.obtenerProductoPorId(idInt)
        }
        cargando = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                title = {
                    Text(
                        text = "Detalle del producto",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (cargando) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            producto?.let { prod ->
                DetalleProductoContenido(
                    producto = prod,
                    onVerMovimientos = {
                        navController.navigate(Route.Entradas_y_Salidas_Productos.path)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Producto no encontrado")
                }
            }
        }
    }
}

@Composable
private fun DetalleProductoContenido(
    producto: Producto,
    onVerMovimientos: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Usamos 5 como stock mínimo de referencia (si aún no lo tienes en BD)
    val stockMinimoReferencia = 5

    val (estadoTexto, estadoColor) = when {
        producto.stock <= 0 -> "Sin stock" to MaterialTheme.colorScheme.error
        producto.stock in 1..stockMinimoReferencia ->
            "Stock bajo" to MaterialTheme.colorScheme.tertiary
        else -> "En stock" to MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Bloque imagen (por ahora placeholder)
        // Bloque imagen: si hay foto, mostrarla; si no, placeholder
        if (producto.imagenUri != null) {
            AsyncImage(
                model = producto.imagenUri,
                contentDescription = "Foto del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory,
                    contentDescription = "Imagen del producto",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre y categoría
        Text(
            text = producto.nombre,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = producto.categoria,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Precio
        Text(
            text = "Precio: $${"%.2f".format(producto.precio)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Proveedor
        Text(
            text = "Proveedor: ${producto.proveedor}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stock + estado
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Inventario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Stock actual: ${producto.stock}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Stock de referencia (bajo): <= $stockMinimoReferencia",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(estadoTexto) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = estadoColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = producto.descripcion,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para ver movimientos
        Button(
            onClick = onVerMovimientos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver movimientos de inventario")
        }
    }
}
