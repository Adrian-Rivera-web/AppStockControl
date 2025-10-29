package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.data.local.user.UserEntity
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel, // Recibir el ViewModel de usuario
    adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(
            UserRepository(
                AppDatabase.getInstance(LocalContext.current).userDao()
            )
        )
    )
) {
    // Verificar si el usuario actual es administrador
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()
    println("DEBUG: UsuarioScreen - esAdmin: $esAdmin")
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()

    // Si no es admin, redirigir a home
    if (!esAdmin) {
        LaunchedEffect(Unit) {
            navController.navigate(Route.Home.path) {
                popUpTo(Route.Usuario.path) { inclusive = true }
            }
        }
        return
    }

    val uiState by adminViewModel.uiState.collectAsState()
    var usuarioAEliminar by remember { mutableStateOf<UserEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Diálogo de confirmación para eliminar usuario
    if (usuarioAEliminar != null) {
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Confirmar Eliminación") },
            text = {
                Text("¿Estás seguro de que quieres eliminar al usuario ${usuarioAEliminar?.name}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        usuarioAEliminar?.let { usuario ->
                            coroutineScope.launch {
                                adminViewModel.eliminarUsuario(usuario.id)
                                usuarioAEliminar = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { usuarioAEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            // ✅ CORREGIDO: Usar TopAppBar en lugar de CenterAlignedTopAppBar
            TopAppBar(
                title = {
                    Text(
                        "Administrar Usuarios",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { adminViewModel.cargarUsuarios() },
                icon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Recargar"
                    )
                },
                text = { Text("Recargar") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Información del administrador actual
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Administrador: ${usuarioLogueado ?: "Sesión activa"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total de usuarios: ${uiState.usuarios.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Mostrar error si existe
            uiState.error?.let { error ->
                AlertDialog(
                    onDismissRequest = { adminViewModel.limpiarError() },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        Button(
                            onClick = { adminViewModel.limpiarError() }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }

            if (uiState.cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando usuarios...")
                    }
                }
            } else if (uiState.usuarios.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Sin usuarios",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay usuarios registrados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Lista de usuarios
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.usuarios) { usuario ->
                        UserCard(
                            usuario = usuario,
                            onDelete = {
                                usuarioAEliminar = usuario // Mostrar diálogo de confirmación
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    usuario: UserEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (usuario.isAdmin)
                        Icons.Default.AdminPanelSettings
                    else
                        Icons.Default.Person,
                    contentDescription = "Tipo de usuario",
                    tint = if (usuario.isAdmin) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = usuario.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Email: ${usuario.email}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Teléfono: ${usuario.phone}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Dirección: ${usuario.address}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Solo permitir eliminar usuarios no administradores
                if (!usuario.isAdmin) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                } else {
                    Text(
                        text = "Usuario Administrador",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}