package com.example.appstockcontrol_grupo_07.ui.screen

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProveedorRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioProductoViewModelFactory
import kotlinx.coroutines.delay

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
    val proveedorRepository = ProveedorRepository(database.proveedorDao())

    val viewModel: FormularioProductoViewModel = viewModel(
        factory = FormularioProductoViewModelFactory(productoRepository, categoriaRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val categoriasExistentes by viewModel.categoriasExistentes.collectAsState()

    // Proveedores desde Room
    val proveedores by remember {
        proveedorRepository.obtenerProveedores()
    }.collectAsState(initial = emptyList())

    var mostrarPantallaExito by remember { mutableStateOf(false) }

    // 📸 Estado para manejar la imagen y la cámara
    var imagenUriSeleccionada by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imagenUriSeleccionada != null) {
            viewModel.onImagenUriChange(imagenUriSeleccionada.toString())
        }
    }

    fun crearUriImagen(): Uri? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "producto_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    val permisoCamara = Manifest.permission.CAMERA

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = crearUriImagen()
            if (uri != null) {
                imagenUriSeleccionada = uri
                cameraLauncher.launch(uri)
            }
        } else {
            // aquí podrías mostrar un Toast si quieres
        }
    }

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
                        text = if (productoId != null && productoId != "0") "Editar Producto" else "Nuevo Producto",
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

                // Campo Stock mínimo
                OutlinedTextField(
                    value = uiState.stockMinimo,
                    onValueChange = { viewModel.onStockMinimoChange(it) },
                    label = { Text("Stock mínimo (opcional)") },
                    placeholder = { Text("Ej: 5") },
                    isError = uiState.errores.stockMinimo != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.errores.stockMinimo != null) {
                    Text(
                        text = uiState.errores.stockMinimo ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🟡 Campo Categoría como lista desplegable (solo lectura)
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                var categoriaExpandida by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = categoriaExpandida,
                    onExpandedChange = { categoriaExpandida = !categoriaExpandida },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.categoria,
                        onValueChange = { /* no editable */ },
                        readOnly = true,
                        label = { Text("Categoría") },
                        placeholder = { Text("Selecciona una categoría") },
                        isError = uiState.errores.categoria != null,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = categoriaExpandida,
                        onDismissRequest = { categoriaExpandida = false }
                    ) {
                        categoriasExistentes.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.onCategoriaChange(cat)
                                    categoriaExpandida = false
                                }
                            )
                        }
                    }
                }

                if (uiState.errores.categoria != null) {
                    Text(
                        text = uiState.errores.categoria ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🟢 Campo Proveedor como lista desplegable (solo lectura)
                val nombresProveedores = proveedores.map { it.nombre }.sorted()

                Text(
                    text = "Proveedor",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                var proveedorExpandido by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = proveedorExpandido,
                    onExpandedChange = { proveedorExpandido = !proveedorExpandido },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.proveedor,
                        onValueChange = { /* no editable */ },
                        readOnly = true,
                        label = { Text("Proveedor") },
                        isError = uiState.errores.proveedor != null,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = proveedorExpandido)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = proveedorExpandido,
                        onDismissRequest = { proveedorExpandido = false }
                    ) {
                        nombresProveedores.forEach { nombre ->
                            DropdownMenuItem(
                                text = { Text(nombre) },
                                onClick = {
                                    viewModel.onProveedorChange(nombre)
                                    proveedorExpandido = false
                                }
                            )
                        }
                    }
                }

                if (uiState.errores.proveedor != null) {
                    Text(
                        text = uiState.errores.proveedor ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 📸 SECCIÓN: Foto del producto (recurso nativo cámara)
                Text(
                    text = "Foto del producto (opcional)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    permisoCamara
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val uri = crearUriImagen()
                                if (uri != null) {
                                    imagenUriSeleccionada = uri
                                    cameraLauncher.launch(uri)
                                }
                            } else {
                                requestCameraPermissionLauncher.launch(permisoCamara)
                            }
                        }
                    ) {
                        Text("Tomar foto")
                    }

                    if (uiState.imagenUri != null) {
                        Text(
                            text = "Foto guardada ✅",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
                    // Cancelar
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    // Guardar
                    Button(
                        onClick = {
                            if (productoId != null && productoId != "0") {
                                viewModel.actualizarProducto(productoId.toInt()) {
                                    mostrarPantallaExito = true
                                }
                            } else {
                                viewModel.guardarProducto {
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
                    delay(1000)
                    mostrarPantallaExito = false
                    navController.navigate(Route.ListaProductos.path) {
                        popUpTo(Route.ListaProductos.path) { inclusive = true }
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
                            text = "Producto guardado con éxito",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
