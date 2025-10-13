package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import kotlin.let

@Composable
fun LoginScreen(
    navController: NavController,          // Controla la navegación entre pantallas
    viewModel: UsuarioViewModel            // ViewModel que mantiene el estado de la pantalla
) {
    // Obtenemos el estado actual del formulario desde el ViewModel
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier.padding(all = 16.dp)  // Margen interno de toda la columna
    ) {
        // Título de la pantalla
        Text(
            text = "Inicio Sesion",
            style = MaterialTheme.typography.headlineMedium
        )

        // Campo de texto para el correo electrónico
        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,  // Actualiza el estado en ViewModel
            label = { Text(text = "Correo electrónico") },
            isError = estado.errores.correo != null,    // Indica error si existe
            supportingText = {
                estado.errores.correo?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()          // Ocupa todo el ancho disponible
        )

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto
            isError = estado.errores.clave != null,
            supportingText = {
                estado.errores.clave?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ Row para colocar los botones lado a lado
        Row(
            modifier = Modifier.fillMaxWidth(),                 // La fila ocupa todo el ancho
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp) // Espacio entre botones
        ) {
            // Botón "Inicio Sesion"
            Button(
                onClick = { navController.navigate(route = "home") }, // Navega a Home al presionar
                modifier = Modifier.weight(1f)                        // Ocupa la mitad del Row
            ) {
                Text(text = "Inicio Sesion")
            }

            // Botón "Cuenta Nueva"
            Button(
                onClick = { navController.navigate(route = "registro") }, // Navega a Registro
                modifier = Modifier.weight(1f)                              // Ocupa la otra mitad del Row
            ) {
                Text(text = "Cuenta Nueva")
            }
        }
    }
}
