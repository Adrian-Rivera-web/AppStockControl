package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.viewmodel.ReportesInventarioViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ReportesInventarioViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesInventarioScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val productoRepository = ProductoRepository(database.productoDao())

    val viewModel: ReportesInventarioViewModel = viewModel(
        factory = ReportesInventarioViewModelFactory(productoRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes de Inventario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Subtítulo
            Text(
                text = "Resumen del estado actual del inventario",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            if (uiState.cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            // 📊 Tarjetas con indicadores
            IndicadoresInventario(uiState = uiState)

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Productos más críticos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Ordenados por prioridad según stock disponible y mínimo configurado.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            if (uiState.topProductosBajoStock.isEmpty()) {
                Text(
                    text = "No hay productos con stock bajo configurado.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.topProductosBajoStock) { producto ->
                        ProductoCriticoItem(producto = producto)
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadoresInventario(uiState: com.example.appstockcontrol_grupo_07.viewmodel.ReportesInventarioUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Fila 1: Total productos + unidades totales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total de productos", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = uiState.totalProductos.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Inventory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Unidades totales", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = uiState.totalUnidades.toString(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Warehouse,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Fila 2: Sin stock + Stock bajo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Sin stock", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = uiState.productosSinStock.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Stock bajo", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = uiState.productosBajoMinimo.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Fila 3: Valor total
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("Valor total del inventario", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "$${"%.2f".format(uiState.valorTotalInventario)}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun ProductoCriticoItem(producto: Producto) {
    val esSinStock = producto.stock <= 0
    val esBajoMinimo = !esSinStock &&
            producto.stockMinimo > 0 &&
            producto.stock <= producto.stockMinimo

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when {
                esSinStock -> MaterialTheme.colorScheme.errorContainer
                esBajoMinimo -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(producto.nombre, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Stock: ${producto.stock} (min: ${producto.stockMinimo})",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Categoría: ${producto.categoria} | Proveedor: ${producto.proveedor}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
