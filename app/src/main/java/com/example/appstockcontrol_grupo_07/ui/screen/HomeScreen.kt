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
fun HomeScreen(
    navController: NavController,
    viewModel: UsuarioViewModel) {
    val estado by viewModel.estado.collectAsState()

    Column(Modifier.padding(all = 16.dp)) {
        Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = {
                navController.navigate(route = "login")

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cerrar Sesion")
        }
    }
}