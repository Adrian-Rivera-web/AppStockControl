package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

@Composable
fun HomeAdminScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    onHome: () -> Unit = {},
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

    println("DEBUG: HomeAdminScreen - Usuario: $usuarioLogueado, esAdmin: $esAdmin")

    // ✅ VERIFICAR SI ES ADMINISTRADOR - SI NO LO ES, REDIRIGIR
    if (!esAdmin && usuarioLogueado != null) {
        LaunchedEffect(Unit) {
            println("DEBUG: HomeAdminScreen - No es admin, redirigiendo a Home")
            navController.navigate(Route.Home.path) {
                popUpTo(Route.HomeAdmin.path) { inclusive = true }
            }
        }
        return
    }

    Column(Modifier.padding(all = 16.dp)) {
        Text(
            text = "Bienvenido Admin: ${usuarioLogueado ?: "Usuario"}",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                navController.navigate(Route.Productos.path)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Productos")
        }

        // Botón para administrar usuarios
        Button(
            onClick = {
                println("DEBUG: HomeAdminScreen - Navegando a UsuarioScreen")
                navController.navigate(Route.Usuario.path)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuarios"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Administrar Usuarios")
        }

        // ✅ CORREGIDO: Cierre de sesión más seguro
        Button(
            onClick = {
                println("DEBUG: HomeAdminScreen - Iniciando cierre de sesión")
                usuarioViewModel.cerrarSesion()

                // Navegar a Login de manera segura
                navController.navigate(Route.Login.path) {
                    // Limpiar la pila de navegación hasta la raíz
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    // Evitar múltiples copias de Login
                    launchSingleTop = true
                }
                println("DEBUG: HomeAdminScreen - Navegación a Login completada")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}