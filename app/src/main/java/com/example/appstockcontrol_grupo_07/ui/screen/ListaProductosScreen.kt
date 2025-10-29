package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.ListaProductosViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ListaProductosViewModelFactory
import androidx.compose.foundation.layout.Spacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val productoRepository = remember { ProductoRepository(database.productoDao()) }
    val viewModel: ListaProductosViewModel = viewModel(
        factory = ListaProductosViewModelFactory(productoRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Efecto para buscar cuando cambia el query
    LaunchedEffect(searchQuery) {
        viewModel.buscarProductos(searchQuery)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Route.FormularioProducto.path)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // ✅ BOTÓN PARA VOLVER AL HOME ADMIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Route.HomeAdmin.path)
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver al Home Admin"
                    )
                }
                Text(
                    text = "Volver al Home",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lista de Productos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.productos.size} productos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar productos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Mostrar error si existe
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        IconButton(onClick = { viewModel.limpiarError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar error",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Estado de carga
            if (uiState.cargando || uiState.buscando) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = if (uiState.buscando) "Buscando..." else "Cargando productos...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else if (uiState.productos.isEmpty()) {
                // Estado vacío
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = "Sin productos",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron productos" else "No hay productos",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    if (searchQuery.isNotEmpty()) {
                        Button(
                            onClick = {
                                searchQuery = ""
                                viewModel.limpiarBusqueda()
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Limpiar búsqueda")
                        }
                    }
                }
            } else {
                // Lista de productos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onProductoClick = {
                                // Navegar a edición del producto
                                navController.navigate("${Route.FormularioProducto.path}?productoId=${producto.id}")
                            },
                            onEliminarClick = {
                                viewModel.eliminarProducto(producto.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCard(
    producto: com.example.appstockcontrol_grupo_07.model.Producto,
    onProductoClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        onClick = onProductoClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = producto.categoria,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Stock: ${producto.stock}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (producto.stock > 10) MaterialTheme.colorScheme.primary
                        else if (producto.stock > 0) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${"%.2f".format(producto.precio)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onEliminarClick
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar producto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}