package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
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
import com.example.appstockcontrol_grupo_07.viewmodel.LoginViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.LoginViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

// 🔔 IMPORTS para vibración
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context

@Composable
fun LoginScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            UserRepository(
                AppDatabase.getInstance(LocalContext.current).userDao()
            )
        )
    )
) {
    val estado by loginViewModel.estado.collectAsState()
    var mostrarClave by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 🔔 Función local para vibrar cuando haya un error
    fun vibrarError() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    150L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150L)
        }
    }

    Column(
        modifier = Modifier.padding(all = 16.dp)
    ) {
        Text(
            text = "Inicio Sesion",
            style = MaterialTheme.typography.headlineMedium
        )

        // Campo correo
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

        // Campo contraseña
        OutlinedTextField(
            value = estado.clave,
            onValueChange = loginViewModel::onClaveChange,
            label = { Text(text = "Contraseña") },
            visualTransformation = if (mostrarClave) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Error de autenticación (usuario/clave incorrectos)
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
            // Cuenta nueva
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

            // 🔔 Botón de inicio de sesión con vibración
            Button(
                onClick = {
                    println("DEBUG: LoginScreen - Botón de login presionado")

                    // 1️⃣ Primero validamos el formulario
                    val esValido = loginViewModel.validarLogin()
                    if (!esValido) {
                        // Si los campos están mal (correo inválido, clave corta, etc.) → vibrar y salir
                        vibrarError()
                        return@Button
                    }

                    // 2️⃣ Si el formulario es válido, intentamos autenticar
                    loginViewModel.autenticarUsuario { exitoso, nombreUsuario, esAdmin ->
                        println("DEBUG: LoginScreen - Callback recibido: exitoso=$exitoso, nombre=$nombreUsuario, esAdmin=$esAdmin")

                        if (exitoso) {
                            usuarioViewModel.iniciarSesion(
                                correo = estado.correo,
                                esAdmin = esAdmin,
                                nombre = nombreUsuario ?: "Usuario"
                            )

                            if (esAdmin) {
                                navController.navigate(Route.HomeAdmin.path) {
                                    popUpTo(Route.Login.path) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Route.Home.path) {
                                    popUpTo(Route.Login.path) { inclusive = true }
                                }
                            }
                        } else {
                            println("DEBUG: LoginScreen - Autenticación fallida")
                            // 3️⃣ Si el usuario/clave no coinciden → vibrar también
                            vibrarError()
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

        // Info extra
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
/**
 * 🔔 Botón reutilizable que:
 *  - Ejecuta onGuardar()
 *  - Si tieneErrores == true → hace vibrar el dispositivo
 *  - Puede mostrar spinner si mostrandoCarga == true
 */
@Composable
fun BotonGuardarConVibrador(
    tieneErrores: Boolean,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    texto: String = "Guardar",
    mostrandoCarga: Boolean = false
) {
    val context = LocalContext.current

    fun vibrarError() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    150L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150L)
        }
    }

    Button(
        onClick = {
            onGuardar()
            if (tieneErrores) {
                vibrarError()
            }
        },
        enabled = enabled,
        modifier = modifier
    ) {
        if (mostrandoCarga) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
                Text(texto)
            }
        } else {
            Text(texto)
        }
    }
}
