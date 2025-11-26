package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
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
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioCategoriaViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioCategoriaViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCategoriaScreen(
    navController: NavController,
    categoriaId: String? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val categoriaRepository = CategoriaRepository(database.categoriaDao())
    val viewModel: FormularioCategoriaViewModel = viewModel(
        factory = FormularioCategoriaViewModelFactory(categoriaRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    var mostrarPantallaExito by remember { mutableStateOf(false) }

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
                        text = if (categoriaId != null && categoriaId != "0") "Editar Categoría" else "Nueva Categoría",
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // ===== CONTENIDO DEL FORMULARIO =====
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                // Error general
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
                                    mostrarPantallaExito = true
                                }
                            } else {
                                viewModel.guardarCategoria {
                                    mostrarPantallaExito = true
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

            // ===== OVERLAY DE ÉXITO =====
            if (mostrarPantallaExito) {
                LaunchedEffect(Unit) {
                    delay(1500) // 1.5 segundos
                    mostrarPantallaExito = false
                    navController.navigate(Route.ListaCategoria.path) {
                        popUpTo(Route.ListaCategoria.path) { inclusive = true }
                    }
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.97f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Éxito",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Categoría guardada con éxito",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
