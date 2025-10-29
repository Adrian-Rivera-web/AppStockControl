package com.example.appstockcontrol_grupo_07.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier,
                colors = NavigationDrawerItemDefaults.colors()
            )
        }
    }
}

// ✅ NUEVA función para ítems del drawer de administrador
@Composable
fun adminDrawerItems(
    onHome: () -> Unit,
    onListaUsuarios: () -> Unit,
    onListaProductos: () -> Unit,
    onFormularioProducto: () -> Unit,
    onListaCategorias: () -> Unit,
    onFormularioCategoria: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Lista Usuarios", Icons.Filled.Group, onListaUsuarios),
    DrawerItem("Lista Productos", Icons.Filled.Inventory, onListaProductos),
    DrawerItem("Formulario Producto", Icons.Filled.Add, onFormularioProducto),
    DrawerItem("Lista Categorías", Icons.Filled.Category, onListaCategorias),
    DrawerItem("Formulario Categoría", Icons.Filled.Add, onFormularioCategoria)
)

// ✅ Función para ítems del drawer de usuario normal
@Composable
fun userDrawerItems(
    onHome: () -> Unit,
    onListaProductos: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Lista Productos", Icons.Filled.Inventory, onListaProductos)
)

// ✅ Mantener la función original para cuando no hay usuario logueado
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),
    DrawerItem("Login", Icons.Filled.Person, onLogin),
    DrawerItem("Registro", Icons.Filled.Person, onRegister)
)