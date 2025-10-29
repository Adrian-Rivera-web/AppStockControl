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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment  // ✅ AÑADIR ESTA IMPORTACIÓN TAMBIÉN
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioCategoriaViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioCategoriaViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCategoriaScreen(
    navController: NavController,
    categoriaId: String? = null,
    viewModel: FormularioCategoriaViewModel
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val categoriaRepository = CategoriaRepository(database.categoriaDao())
    val viewModel: FormularioCategoriaViewModel = viewModel(
        factory = FormularioCategoriaViewModelFactory(categoriaRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Cargar categoría si estamos editando
    LaunchedEffect(categoriaId) {
        if (categoriaId != null && categoriaId != "0") {
            viewModel.cargarCategoria(categoriaId.toInt())
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
                        text = if (categoriaId != null) "Editar Categoría" else "Nueva Categoría",
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
                label = { Text("Nombre de la categoría") },
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

            // Checkbox Activa
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.activa,
                    onCheckedChange = { viewModel.onActivaChange(it) }
                )
                Text(
                    text = "Categoría activa",
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
                        if (categoriaId != null && categoriaId != "0") {
                            viewModel.actualizarCategoria(categoriaId.toInt()) {
                                navController.navigate(Route.ListaCategoria.path) {
                                    popUpTo(Route.ListaCategoria.path) { inclusive = true }
                                }
                            }
                        } else {
                            viewModel.guardarCategoria {
                                navController.navigate(Route.ListaCategoria.path) {
                                    popUpTo(Route.ListaCategoria.path) { inclusive = true }
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