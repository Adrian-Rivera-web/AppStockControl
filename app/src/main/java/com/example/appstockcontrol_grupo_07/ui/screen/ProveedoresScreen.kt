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
fun ProveedoresScreen(
    navController: NavController
) {

    Column(Modifier.padding(all = 16.dp)) {
        Text(
            text = "Categorias",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = {
                navController.navigate(Route.ListaProveedores.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lista Proveedores")
        }
        Button(
            onClick = {
                navController.navigate(Route.FormularioProveedores.path)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Formulario Proveedores")
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