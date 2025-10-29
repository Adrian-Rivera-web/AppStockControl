package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
import com.example.appstockcontrol_grupo_07.data.repository.ProveedorRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioProveedorViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioProveedorViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProveedoresScreen(
    navController: NavController,
    proveedorId: String? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val proveedorRepository = ProveedorRepository(database.proveedorDao())
    val viewModel: FormularioProveedorViewModel = viewModel(
        factory = FormularioProveedorViewModelFactory(proveedorRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Cargar proveedor si estamos editando
    LaunchedEffect(proveedorId) {
        if (proveedorId != null && proveedorId != "0") {
            viewModel.cargarProveedor(proveedorId.toInt())
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
                        text = if (proveedorId != null) "Editar Proveedor" else "Nuevo Proveedor",
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
                label = { Text("Nombre del proveedor") },
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

            // Campo Contacto
            OutlinedTextField(
                value = uiState.contacto,
                onValueChange = { viewModel.onContactoChange(it) },
                label = { Text("Persona de contacto") },
                isError = uiState.errores.contacto != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.contacto != null) {
                Text(
                    text = uiState.errores.contacto ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Teléfono
            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                isError = uiState.errores.telefono != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.telefono != null) {
                Text(
                    text = uiState.errores.telefono ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                isError = uiState.errores.email != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.email != null) {
                Text(
                    text = uiState.errores.email ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Dirección
            OutlinedTextField(
                value = uiState.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección") },
                isError = uiState.errores.direccion != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errores.direccion != null) {
                Text(
                    text = uiState.errores.direccion ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox Activo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.activo,
                    onCheckedChange = { viewModel.onActivoChange(it) }
                )
                Text(
                    text = "Proveedor activo",
                    modifier = Modifier.padding(start = 8.dp)
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
                        if (proveedorId != null && proveedorId != "0") {
                            viewModel.actualizarProveedor(proveedorId.toInt()) {
                                navController.navigate(Route.ListaProveedores.path) {
                                    popUpTo(Route.ListaProveedores.path) { inclusive = true }
                                }
                            }
                        } else {
                            viewModel.guardarProveedor {
                                navController.navigate(Route.ListaProveedores.path) {
                                    popUpTo(Route.ListaProveedores.path) { inclusive = true }
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