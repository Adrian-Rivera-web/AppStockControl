package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.LoginViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.LoginViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel, // Recibir el ViewModel compartido
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            UserRepository(
                AppDatabase.getInstance(LocalContext.current).userDao()
            )
        )
    )
) {
    val estado by loginViewModel.estado.collectAsState()

    Column(
        modifier = Modifier.padding(all = 16.dp)
    ) {
        Text(
            text = "Inicio Sesion",
            style = MaterialTheme.typography.headlineMedium
        )

        // Campo de texto para el correo electrónico
        OutlinedTextField(
            value = estado.correo,
            onValueChange = loginViewModel::onCorreoChange,
            label = { Text(text = "Correo electrónico") },
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = estado.clave,
            onValueChange = loginViewModel::onClaveChange,
            label = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.errores.clave != null,
            supportingText = {
                estado.errores.clave?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Mostrar error de autenticación si existe
        estado.errorAutenticacion?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón para ir a Registro
            Button(
                onClick = {
                    loginViewModel.limpiarFormulario()
                    navController.navigate(Route.Register.path)
                },
                modifier = Modifier.weight(1f),
                enabled = !estado.cargando
            ) {
                Text("Cuenta Nueva")
            }

            // Botón para iniciar sesión
            Button(
                onClick = {
                    println("DEBUG: LoginScreen - Botón de login presionado")
                    loginViewModel.autenticarUsuario { exitoso, nombreUsuario, esAdmin ->
                        println("DEBUG: LoginScreen - Callback recibido: exitoso=$exitoso, nombre=$nombreUsuario, esAdmin=$esAdmin")

                        if (exitoso) {
                            // Actualizar el ViewModel compartido
                            usuarioViewModel.iniciarSesion(
                                correo = estado.correo,
                                esAdmin = esAdmin,
                                nombre = nombreUsuario ?: "Usuario"
                            )

                            println("DEBUG: LoginScreen - UsuarioViewModel actualizado, navegando...")

                            // Navegación inmediata
                            if (esAdmin) {
                                println("DEBUG: LoginScreen - 🚀 Navegando a HomeAdmin")
                                navController.navigate(Route.HomeAdmin.path) {
                                    popUpTo(Route.Login.path) { inclusive = true }
                                }
                            } else {
                                println("DEBUG: LoginScreen - 🚀 Navegando a Home")
                                navController.navigate(Route.Home.path) {
                                    popUpTo(Route.Login.path) { inclusive = true }
                                }
                            }
                        } else {
                            println("DEBUG: LoginScreen - Autenticación fallida")
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !estado.cargando
            ) {
                if (estado.cargando) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Cargando...")
                    }
                } else {
                    Text("Inicio Sesion")
                }
            }
        }

        // Información de debug actualizada con usuarios de Room
        Text(
            text = "Usuarios predefinidos:\n" +
                    "• ad.rivera@duocuc.cl / Admin_123 (Admin)\n" +
                    "• fre.rivera@duocuc.cl / Usuario_123 (Usuario)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}