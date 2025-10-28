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
fun HomeScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    onHome: () -> Unit = {},
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()

    Column(Modifier.padding(all = 16.dp)) {
        Text(
            text = "Bienvenido ${usuarioLogueado ?: "Usuario"}",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                navController.navigate(Route.Productos.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Productos")
        }

        Button(
            onClick = {
                navController.navigate(Route.Categoria.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Categoria")
        }

        Button(
            onClick = {
                navController.navigate(Route.Entradas_y_Salidas_Productos.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entradas Y Salidas")
        }

        Button(
            onClick = {
                navController.navigate(Route.Proveedores.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Provedores")
        }

        // ✅ CORREGIDO: Cierre de sesión más seguro
        Button(
            onClick = {
                println("DEBUG: HomeScreen - Iniciando cierre de sesión")
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
                println("DEBUG: HomeScreen - Navegación a Login completada")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}