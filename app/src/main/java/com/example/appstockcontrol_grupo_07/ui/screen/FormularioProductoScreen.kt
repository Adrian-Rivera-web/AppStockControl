package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioProductoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    navController: NavController,
    productoId: String? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)


    val productoRepository = ProductoRepository(database.productoDao())
    val categoriaRepository = CategoriaRepository(database.categoriaDao())

    val viewModel: FormularioProductoViewModel = viewModel(
        factory = FormularioProductoViewModelFactory(productoRepository, categoriaRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val categoriasExistentes by viewModel.categoriasExistentes.collectAsState()

    // Cargar producto si estamos editando
    LaunchedEffect(productoId) {
        if (productoId != null && productoId != "0") {
            viewModel.cargarProducto(productoId.toInt())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                title = {
                    Text(
                        text = if (productoId != null) "Editar Producto" else "Nuevo Producto",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre del producto") },
                isError = uiState.errores.nombre != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.nombre != null) {
                Text(
                    text = uiState.errores.nombre ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Descripción
            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Descripción") },
                isError = uiState.errores.descripcion != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.descripcion != null) {
                Text(
                    text = uiState.errores.descripcion ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Precio
            OutlinedTextField(
                value = uiState.precio,
                onValueChange = { viewModel.onPrecioChange(it) },
                label = { Text("Precio") },
                isError = uiState.errores.precio != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.precio != null) {
                Text(
                    text = uiState.errores.precio ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Stock
            OutlinedTextField(
                value = uiState.stock,
                onValueChange = { viewModel.onStockChange(it) },
                label = { Text("Stock") },
                isError = uiState.errores.stock != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.stock != null) {
                Text(
                    text = uiState.errores.stock ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.categoria,
                        onValueChange = { viewModel.onCategoriaChange(it) },
                        label = { Text("Categoría") },
                        placeholder = { Text("Ej: Electrónicos, Ropa, Hogar...") },
                        isError = uiState.errores.categoria != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (uiState.errores.categoria != null) {
                        Text(
                            text = uiState.errores.categoria ?: "",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (categoriasExistentes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Categorías disponibles:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = categoriasExistentes.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Campo Proveedor
            OutlinedTextField(
                value = uiState.proveedor,
                onValueChange = { viewModel.onProveedorChange(it) },
                label = { Text("Proveedor") },
                isError = uiState.errores.proveedor != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.proveedor != null) {
                Text(
                    text = uiState.errores.proveedor ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar error general si existe
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Cancelar
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                // Botón Guardar
                Button(
                    onClick = {
                        if (productoId != null && productoId != "0") {
                            viewModel.actualizarProducto(productoId.toInt()) {
                                navController.navigate(Route.ListaProductos.path) {
                                    popUpTo(Route.ListaProductos.path) { inclusive = true }
                                }
                            }
                        } else {
                            viewModel.guardarProducto {
                                navController.navigate(Route.ListaProductos.path) {
                                    popUpTo(Route.ListaProductos.path) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.cargando
                ) {
                    Text(if (uiState.cargando) "Guardando..." else "Guardar")
                }
            }
        }
    }
}