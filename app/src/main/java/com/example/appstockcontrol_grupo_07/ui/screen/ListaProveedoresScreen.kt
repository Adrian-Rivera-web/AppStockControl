package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.ProveedorRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.ProveedorViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProveedorViewModelFactory
import com.example.appstockcontrol_grupo_07.model.Proveedor
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProveedoresScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val proveedorRepository = remember { ProveedorRepository(database.proveedorDao()) }
    val viewModel: ProveedorViewModel = viewModel(
        factory = ProveedorViewModelFactory(proveedorRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var proveedorAEliminar by remember { mutableStateOf<Proveedor?>(null) }
    var mostrarDialogoEliminarProveedor by remember { mutableStateOf(false) }

    // Efecto para buscar cuando cambia el query
    LaunchedEffect(searchQuery) {
        viewModel.buscarProveedores(searchQuery)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Route.FormularioProveedores.path)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar proveedor")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Botón para volver al Home Admin
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
                    text = "Lista de Proveedores",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.proveedores.size} proveedores",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar proveedores...") },
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
                        text = if (uiState.buscando) "Buscando..." else "Cargando proveedores...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else if (uiState.proveedores.isEmpty()) {
                // Estado vacío
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = "Sin proveedores",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron proveedores" else "No hay proveedores",
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
                // Lista de proveedores
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.proveedores) { proveedor ->
                        ProveedorCard(
                            proveedor = proveedor,
                            onProveedorClick = {
                                // Navegar a edición, si lo tienes
                                navController.navigate("${Route.FormularioProveedores.path}?proveedorId=${proveedor.id}")
                            },
                            onEliminarClick = {
                                // 👇 en vez de eliminar de inmediato, abrimos el diálogo
                                proveedorAEliminar = proveedor
                                mostrarDialogoEliminarProveedor = true
                            }
                        )
                    }
                }
            }
            if (mostrarDialogoEliminarProveedor && proveedorAEliminar != null) {
                AlertDialog(
                    onDismissRequest = {
                        mostrarDialogoEliminarProveedor = false
                        proveedorAEliminar = null
                    },
                    title = {
                        Text("Eliminar proveedor")
                    },
                    text = {
                        Text(
                            "¿Seguro que deseas eliminar el proveedor \"${proveedorAEliminar?.nombre}\"? " +
                                    "Esta acción no se puede deshacer."
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                proveedorAEliminar?.id?.let { id ->
                                    viewModel.eliminarProveedor(id)
                                }
                                mostrarDialogoEliminarProveedor = false
                                proveedorAEliminar = null
                            }
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                mostrarDialogoEliminarProveedor = false
                                proveedorAEliminar = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorCard(
    proveedor: com.example.appstockcontrol_grupo_07.model.Proveedor,
    onProveedorClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        onClick = onProveedorClick,
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
                    text = proveedor.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = proveedor.contacto,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = proveedor.telefono,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = proveedor.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = if (proveedor.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (proveedor.activo) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            IconButton(
                onClick = onEliminarClick
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar proveedor",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}