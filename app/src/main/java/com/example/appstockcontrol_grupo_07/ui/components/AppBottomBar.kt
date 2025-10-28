package com.example.appstockcontrol_grupo_07.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
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

// ✅ SOLO UNA DEFINICIÓN de esta función
@Composable
fun AppBottomBarV2(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit = {},
    onProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val bottomNavItems = listOf(
        BottomNavItem("Menú", Icons.Filled.Menu, null, true),
        BottomNavItem("Inicio", Icons.Filled.Home, "home"),
        BottomNavItem("Productos", Icons.Filled.ShoppingCart, "productos"),
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
                selected = if (item.isAction) false else currentRoute == item.route,
                onClick = {
                    when {
                        item.isAction -> onOpenDrawer()
                        item.route == "perfil" -> onProfile()
                        else -> item.route?.let { onNavigate(it) }
                    }
                }
            )
        }
    }
}