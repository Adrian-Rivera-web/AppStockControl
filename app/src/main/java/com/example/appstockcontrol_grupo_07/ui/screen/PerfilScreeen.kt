package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import kotlinx.coroutines.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val nombreUsuario by usuarioViewModel.nombreUsuario.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

    // 🧠 Acceso a BD / repositorio
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }
    val scope = rememberCoroutineScope()

    // 🔐 Estados para dialogs y cambio de contraseña
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }
    var mostrarDialogoCambio by remember { mutableStateOf(false) }
    var mostrarExito by remember { mutableStateOf(false) }

    var nuevaClave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val homeRoute = if (esAdmin) Route.HomeAdmin.path else Route.Home.path
                            navController.navigate(homeRoute) {
                                popUpTo(homeRoute) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con avatar + rol + nombre
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rol ARRIBA
                    Text(
                        text = if (esAdmin) "Administrador" else "Usuario",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 8.dp)
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface) // fondo claro
                            .border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Nombre COMPLETO debajo
                    Text(
                        text = nombreUsuario ?: "Nombre Apellido",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Información detallada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información Personal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InfoItem(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = usuarioLogueado ?: "No disponible"
                    )

                    InfoItem(
                        icon = Icons.Filled.Security,
                        label = "Rol",
                        value = if (esAdmin) "Administrador" else "Usuario"
                    )

                    InfoItem(
                        icon = Icons.Filled.Person,
                        label = "Estado",
                        value = "Activo"
                    )
                }
            }

            // Botones de acción (solo cambiar contraseña y cerrar sesión)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 🧩 Cambiar contraseña
                Button(
                    onClick = {
                        nuevaClave = ""
                        confirmarClave = ""
                        mensajeError = null
                        mostrarDialogoConfirmacion = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Security, "Cambiar", modifier = Modifier.size(20.dp))
                    Text("Cambiar Contraseña", modifier = Modifier.padding(start = 8.dp))
                }

                // 🔒 Cerrar sesión
                OutlinedButton(
                    onClick = {
                        usuarioViewModel.cerrarSesion()
                        navController.navigate(Route.Login.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }

    // 🔔 Dialogo de confirmación
    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            title = { Text("Confirmar acción") },
            text = { Text("¿Está seguro de que desea cambiar su contraseña?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoConfirmacion = false
                    mostrarDialogoCambio = true
                }) {
                    Text("Sí, cambiar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 🔐 Dialogo para ingresar nueva contraseña (solo nueva + confirmación)
    if (mostrarDialogoCambio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCambio = false },
            title = { Text("Cambiar contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevaClave,
                        onValueChange = { nuevaClave = it },
                        label = { Text("Nueva contraseña") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = confirmarClave,
                        onValueChange = { confirmarClave = it },
                        label = { Text("Confirmar nueva contraseña") },
                        singleLine = true
                    )
                    if (mensajeError != null) {
                        Text(
                            text = mensajeError!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        nuevaClave.isBlank() || confirmarClave.isBlank() ->
                            mensajeError = "Debe completar ambos campos."
                        nuevaClave != confirmarClave ->
                            mensajeError = "Las contraseñas no coinciden."
                        else -> {
                            val emailActual = usuarioLogueado
                            if (emailActual.isNullOrBlank()) {
                                mensajeError = "No se encontró el usuario actual."
                            } else {
                                // 🚀 Actualizar en Room
                                scope.launch {
                                    val error = userRepository.changePassword(emailActual, nuevaClave)
                                    if (error == null) {
                                        mensajeError = null
                                        mostrarDialogoCambio = false
                                        mostrarExito = true
                                    } else {
                                        mensajeError = error
                                    }
                                }
                            }
                        }
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoCambio = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ✅ Mensaje de éxito
    if (mostrarExito) {
        AlertDialog(
            onDismissRequest = { mostrarExito = false },
            title = { Text("Éxito") },
            text = { Text("Contraseña cambiada correctamente.") },
            confirmButton = {
                TextButton(onClick = { mostrarExito = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Wrapper para usar IconButton de Material3 sin conflictos
@Composable
fun IconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick, content = content)
}
