package com.example.appstockcontrol_grupo_07.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Assessment
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
import androidx.compose.ui.text.font.FontWeight
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
    proveedorViewModel: ProveedorViewModel
) {
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()
    // 👇 NUEVO: leemos también el nombre
    val nombreUsuario by usuarioViewModel.nombreUsuario.collectAsState()

    val productos by productoViewModel.productos.collectAsState()
    val adminState by adminViewModel.uiState.collectAsState()
    val categoriaState by categoriaViewModel.uiState.collectAsState()
    val proveedorState by proveedorViewModel.uiState.collectAsState()

    val categorias = categoriaState.categorias
    val proveedores = proveedorState.proveedores

    // Si no es admin, redirigir
    if (!esAdmin && usuarioLogueado != null) {
        LaunchedEffect(Unit) {
            navController.navigate(Route.Home.path) {
                popUpTo(Route.HomeAdmin.path) { inclusive = true }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Encabezado
        Text(
            text = "Panel de administración",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            // 👇 antes usabas usuarioLogueado (correo), ahora nombreUsuario
            text = "Bienvenido, ${nombreUsuario ?: "Administrador"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección Catálogo
        Text(
            text = "Catálogo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Inventory,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "Productos",
                value = productos.size.toString(),
                subtitle = if (productos.size == 1) "producto" else "productos",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    navController.navigate("${Route.ListaProductos.path}?esAdmin=true")
                }
            )

            DashboardCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Category,
                iconTint = MaterialTheme.colorScheme.tertiary,
                title = "Categorías",
                value = categorias.size.toString(),
                subtitle = if (categorias.size == 1) "categoría" else "categorías",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = {
                    navController.navigate(Route.ListaCategoria.path)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección Personas
        Text(
            text = "Personas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Group,
                iconTint = MaterialTheme.colorScheme.secondary,
                title = "Usuarios",
                value = adminState.usuarios.size.toString(),
                subtitle = if (adminState.usuarios.size == 1) "usuario" else "usuarios",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    navController.navigate(Route.Usuario.path)
                }
            )

            DashboardCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalShipping,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                title = "Proveedores",
                value = proveedores.size.toString(),
                subtitle = if (proveedores.size == 1) "proveedor" else "proveedores",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = {
                    navController.navigate(Route.ListaProveedores.path)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección Análisis
        Text(
            text = "Análisis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = { navController.navigate(Route.ReportesInventario.path) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Reportes de inventario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reportes de inventario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ver stock total, productos sin stock y stock bajo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    value: String,
    subtitle: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
