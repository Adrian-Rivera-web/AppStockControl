package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

@Composable
fun PerfilScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val nombreUsuario by usuarioViewModel.nombreUsuario.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

    Column(Modifier.padding(all = 16.dp)) {
        Text(
            text = "Perfil de Usuario",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Nombre: ${nombreUsuario ?: "No disponible"}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Email: ${usuarioLogueado ?: "No disponible"}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Rol: ${if (esAdmin) "Administrador" else "Usuario Normal"}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Botón para volver al home correcto según el rol
        Button(
            onClick = {
                val homeRoute = if (esAdmin) Route.HomeAdmin.path else Route.Home.path
                navController.navigate(homeRoute) {
                    popUpTo(homeRoute) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Volver al Home")
        }

        // Botón de cierre de sesión
        Button(
            onClick = {
                usuarioViewModel.cerrarSesion()
                navController.navigate(Route.Login.path) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}