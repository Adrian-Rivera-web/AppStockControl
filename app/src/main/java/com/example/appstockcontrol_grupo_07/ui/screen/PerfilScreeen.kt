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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val nombreUsuario by usuarioViewModel.nombreUsuario.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

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
            // Header con avatar
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
                    // Avatar del usuario
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = nombreUsuario ?: "Usuario",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = if (esAdmin) "Administrador" else "Usuario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
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

                    // Item de email
                    InfoItem(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = usuarioLogueado ?: "No disponible"
                    )

                    // Item de rol
                    InfoItem(
                        icon = Icons.Filled.Security,
                        label = "Rol",
                        value = if (esAdmin) "Administrador" else "Usuario Normal"
                    )

                    // Item de estado
                    InfoItem(
                        icon = Icons.Filled.Person,
                        label = "Estado",
                        value = "Activo" // Podrías agregar este campo al ViewModel
                    )
                }
            }

            // Botones de acción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para editar perfil (si implementas esta funcionalidad)
                Button(
                    onClick = {
                        // Navegar a pantalla de edición de perfil
                        // navController.navigate("editarPerfil")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false // Deshabilitado hasta que implementes la funcionalidad
                ) {
                    Icon(Icons.Filled.Edit, "Editar", modifier = Modifier.size(20.dp))
                    Text("Editar Perfil", modifier = Modifier.padding(start = 8.dp))
                }

                // Botón de cierre de sesión
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

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Necesitas agregar este Composable para el IconButton
@Composable
fun IconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick, content = content)
}