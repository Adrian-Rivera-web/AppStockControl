package com.example.appstockcontrol_grupo_07.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String? = null,
    val isAction: Boolean = false
)

@Composable
fun AppBottomBarV2(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isAdmin: Boolean, // ✅ Recibir estado de admin
    onProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // Definir rutas dinámicamente según el rol
    val homeRoute = if (isAdmin) "homeAdmin" else "home"

    val bottomNavItems = listOf(
        BottomNavItem("Productos", Icons.Filled.ShoppingCart, "productos"),
        BottomNavItem("Inicio", Icons.Filled.Home, homeRoute), // ✅ Ruta dinámica
        BottomNavItem("Perfil", Icons.Filled.Person, "perfil")
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "perfil") {
                        onProfile()
                    } else {
                        item.route?.let { onNavigate(it) }
                    }
                }
            )
        }
    }
}