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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.CategoriaViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProveedorViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    productoViewModel: ProductoViewModel, // ✅ Nuevo parámetro
    categoriaViewModel: CategoriaViewModel, // ✅ Nuevo parámetro
    proveedorViewModel: ProveedorViewModel // ✅ Nuevo parámetro
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

    // ✅ Obtener datos para los cuadros
    val productos by productoViewModel.productos.collectAsState()
    val categoriaState by categoriaViewModel.uiState.collectAsState()
    val proveedorState by proveedorViewModel.uiState.collectAsState()

    val categorias = categoriaState.categorias
    val proveedores = proveedorState.proveedores

    Column(Modifier.padding(all = 16.dp)) {
        Text(
            text = "Bienvenido ${usuarioLogueado ?: "Usuario"}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Rol: ${if (esAdmin) "Administrador" else "Usuario Normal"}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // ✅ CUADROS DE ESTADÍSTICAS (NO CLICKEABLES)
        Text(
            text = "Resumen General",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Fila 1: Productos y Categorías
        Row(modifier = Modifier.fillMaxWidth()) {
            // ✅ CUADRO DE PRODUCTOS (NO CLICKEABLE)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
                // ❌ SIN onClick - no es clickeable
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

            // ✅ CUADRO DE CATEGORÍAS (NO CLICKEABLE)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
                // ❌ SIN onClick - no es clickeable
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

        // ✅ CUADRO DE PROVEEDORES (NO CLICKEABLE)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
            // ❌ SIN onClick - no es clickeable
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
                    text = proveedores.size.toString(),
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

        Spacer(modifier = Modifier.padding(16.dp))

    }
}
