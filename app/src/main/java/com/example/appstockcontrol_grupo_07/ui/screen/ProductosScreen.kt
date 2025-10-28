package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.navigation.Route



@Composable
fun ProductosScreen(
    navController: NavController
) {

    Column(Modifier.padding(all = 16.dp)) {
        Text(
            text = "Productos",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = {
                navController.navigate(Route.ListaProductos.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lista de Productos")
        }
        Button(
            onClick = {
                navController.navigate(Route.FormularioProducto.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Formulario")
        }

        Button(
            onClick = {
                navController.navigate(Route.Home.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Inicio")
        }
    }
}