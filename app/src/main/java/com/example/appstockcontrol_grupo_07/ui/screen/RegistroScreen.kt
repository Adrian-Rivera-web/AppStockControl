package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.RegistroViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.RegistroViewModelFactory

@Composable
fun RegistroScreen(
    navController: NavController,
    registroViewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(
            UserRepository(
                AppDatabase.getInstance(LocalContext.current).userDao()
            )
        )
    )
) {
    val estado by registroViewModel.estado.collectAsState()
    val isLoading by registroViewModel.isLoading.collectAsState()

    // Estados para controlar visibilidad de contraseñas
    var mostrarClave by remember { mutableStateOf(false) }
    var mostrarClaveConfirmacion by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {
        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium
        )

        // Campo nombre
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = registroViewModel::onNombreChange,
            label = { Text(text = "Nombre completo") },
            isError = estado.errores.nombre != null,
            supportingText = {
                estado.errores.nombre?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo correo
        OutlinedTextField(
            value = estado.correo,
            onValueChange = registroViewModel::onCorreoChange,
            label = { Text(text = "Correo electrónico") },
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo teléfono
        OutlinedTextField(
            value = estado.telefono,
            onValueChange = registroViewModel::onTelefonoChange,
            label = { Text(text = "Teléfono") },
            isError = estado.errores.telefono != null,
            supportingText = {
                estado.errores.telefono?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo clave - MODIFICADO
        OutlinedTextField(
            value = estado.clave,
            onValueChange = registroViewModel::onClaveChange,
            label = { Text(text = "Contraseña") },
            visualTransformation = if (mostrarClave) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { mostrarClave = !mostrarClave }) {
                    Icon(
                        imageVector = if (mostrarClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (mostrarClave) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = estado.errores.clave != null,
            supportingText = {
                estado.errores.clave?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo confirmación de contraseña - MODIFICADO
        OutlinedTextField(
            value = estado.claveConfirmacion,
            onValueChange = registroViewModel::onClaveConfirmacionChange,
            label = { Text(text = "Confirmar Contraseña") },
            visualTransformation = if (mostrarClaveConfirmacion) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { mostrarClaveConfirmacion = !mostrarClaveConfirmacion }) {
                    Icon(
                        imageVector = if (mostrarClaveConfirmacion) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (mostrarClaveConfirmacion) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = estado.errores.claveConfirmacion != null,
            supportingText = {
                estado.errores.claveConfirmacion?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo dirección
        OutlinedTextField(
            value = estado.direccion,
            onValueChange = registroViewModel::onDireccionChange,
            label = { Text(text = "Dirección") },
            isError = estado.errores.direccion != null,
            supportingText = {
                estado.errores.direccion?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Checkbox: aceptar términos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado.aceptaTerminos,
                onCheckedChange = registroViewModel::onAceptarTerminosChange
            )
            Spacer(Modifier.width(width = 8.dp))
            Text(text = "Acepto los términos y condiciones")
        }

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón para cancelar/volver
            Button(
                onClick = {
                    registroViewModel.limpiarFormulario()
                    navController.navigate(Route.Login.path)
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }

            // Botón: enviar registro
            Button(
                onClick = {
                    registroViewModel.registrarUsuario { exitoso, mensaje ->
                        if (exitoso) {
                            // Éxito - limpiar y navegar a login
                            registroViewModel.limpiarFormulario()
                            navController.navigate(Route.Login.path)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Registrando...")
                    }
                } else {
                    Text(text = "Registrar")
                }
            }
        }

        // Información para el usuario
        Text(
            text = "⚠️ Después de registrar, podrás iniciar sesión con el mismo correo y contraseña",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}