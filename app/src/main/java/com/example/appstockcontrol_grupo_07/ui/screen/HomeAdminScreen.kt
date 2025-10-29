package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.navigation.Route
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.CategoriaViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProveedorViewModel

@Composable
fun HomeAdminScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    productoViewModel: ProductoViewModel,
    adminViewModel: AdminViewModel,
    categoriaViewModel: CategoriaViewModel,
    proveedorViewModel: ProveedorViewModel // Nuevo parámetro
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

    // Obtener la lista de productos para contar la cantidad
    val productos by productoViewModel.productos.collectAsState()

    // Obtener la lista de usuarios del AdminViewModel
    val adminState by adminViewModel.uiState.collectAsState()

    // Obtener el estado de categorías
    val categoriaState by categoriaViewModel.uiState.collectAsState()

    // Obtener el estado de proveedores
    val proveedorState by proveedorViewModel.uiState.collectAsState()

    // Extraer las listas del estado
    val categorias = categoriaState.categorias
    val proveedores = proveedorState.proveedores

    println("DEBUG: HomeAdminScreen - Usuario: $usuarioLogueado, esAdmin: $esAdmin")
    println("DEBUG: HomeAdminScreen - Cantidad de productos: ${productos.size}")
    println("DEBUG: HomeAdminScreen - Cantidad de usuarios: ${adminState.usuarios.size}")
    println("DEBUG: HomeAdminScreen - Cantidad de categorías: ${categorias.size}")
    println("DEBUG: HomeAdminScreen - Cantidad de proveedores: ${proveedores.size}")

    // VERIFICAR SI ES ADMINISTRADOR - SI NO LO ES, REDIRIGIR
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

        Spacer(modifier = Modifier.padding(8.dp))

        // FILA 1: PRODUCTOS Y CATEGORÍAS
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // CAJITA DE PRODUCTOS (CLICKEABLE)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = {
                    navController.navigate("${Route.ListaProductos.path}?esAdmin=true")
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "Total Productos",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "Productos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = productos.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = if (productos.size == 1) "producto" else "productos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // CAJITA DE CATEGORÍAS (CLICKEABLE)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                onClick = {
                    navController.navigate(Route.ListaCategoria.path)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = "Total Categorías",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    Text(
                        text = categorias.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Text(
                        text = if (categorias.size == 1) "categoría" else "categorías",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // FILA 2: USUARIOS Y PROVEEDORES
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // CAJITA DE USUARIOS (CLICKEABLE)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                onClick = {
                    navController.navigate(Route.Usuario.path)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Total Usuarios",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "Usuarios",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Text(
                        text = adminState.usuarios.size.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = if (adminState.usuarios.size == 1) "usuario" else "usuarios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // CAJITA DE PROVEEDORES (CLICKEABLE)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = {
                    navController.navigate(Route.ListaProveedores.path)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "Proveedores",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "Proveedores",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = proveedores.size.toString(), // Mostrar la cantidad de proveedores
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (proveedores.size == 1) "proveedor" else "proveedores",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}