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
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

@Composable
fun PerfilScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel, // Recibir el ViewModel compartido
    onHome: () -> Unit = {}
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val nombreUsuario by usuarioViewModel.nombreUsuario.collectAsState()

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

        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Home")
        }
    }
}