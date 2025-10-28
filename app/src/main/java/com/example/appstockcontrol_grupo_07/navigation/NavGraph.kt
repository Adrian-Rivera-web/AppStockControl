package com.example.appstockcontrol_grupo_07.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appstockcontrol_grupo_07.ui.components.AppBottomBarV2
import com.example.appstockcontrol_grupo_07.ui.components.AppDrawer
import com.example.appstockcontrol_grupo_07.ui.components.AppTopBar
import com.example.appstockcontrol_grupo_07.ui.components.defaultDrawerItems
import com.example.appstockcontrol_grupo_07.ui.screen.HomeScreen
import com.example.appstockcontrol_grupo_07.ui.screen.HomeAdminScreen
import com.example.appstockcontrol_grupo_07.ui.screen.LoginScreen
import com.example.appstockcontrol_grupo_07.ui.screen.PerfilScreen
import com.example.appstockcontrol_grupo_07.ui.screen.RegistroScreen
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstockcontrol_grupo_07.ui.screen.CategoriaScreen
import com.example.appstockcontrol_grupo_07.ui.screen.EntradasScreen
import com.example.appstockcontrol_grupo_07.ui.screen.Entradas_y_Salidas_ProductosScreen
import com.example.appstockcontrol_grupo_07.ui.screen.FormularioCategoriaScreen
import com.example.appstockcontrol_grupo_07.ui.screen.FormularioProductoScreen
import com.example.appstockcontrol_grupo_07.ui.screen.FormularioProveedoresScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ListaCategoriaScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ListaProductosScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ListaProveedoresScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ProductosScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ProveedoresScreen
import com.example.appstockcontrol_grupo_07.ui.screen.SalidasScreen
import com.example.appstockcontrol_grupo_07.ui.screen.UsuarioScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    usuarioViewModel: UsuarioViewModel // Recibir el ViewModel desde MainActivity
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Estado del drawer y coroutine scope
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Función helper para navegación
    val navigateTo: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    // Determinar cuándo mostrar las barras
    val mostrarTopBar = when (currentRoute) {
        Route.Home.path, Route.HomeAdmin.path -> true
        else -> false
    }

    val mostrarBottomBar = when (currentRoute) {
        Route.Home.path, Route.HomeAdmin.path -> true
        else -> false
    }

    // CORREGIDO: Declarar openDrawer como función lambda
    val openDrawer: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }

    // Función para cerrar el drawer
    val closeDrawer: () -> Unit = {
        scope.launch {
            drawerState.close()
        }
    }

    // Lista de ítems para el drawer
    val drawerItems = defaultDrawerItems(
        onHome = {
            navigateTo(Route.Home.path)
            closeDrawer()
        },
        onLogin = {
            navigateTo(Route.Login.path)
            closeDrawer()
        },
        onRegister = {
            navigateTo(Route.Register.path)
            closeDrawer()
        }
    )

    // Usar ModalNavigationDrawer para el drawer lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarBottomBar,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                items = drawerItems,
                modifier = Modifier
            )
        }
    ) {
        Scaffold(
            topBar = {
                // MOSTRAR TopBar solo en Home y HomeAdmin
                if (mostrarTopBar) {
                    AppTopBar(
                        onOpenDrawer = openDrawer, // Ahora es una función válida
                        onHome = { navigateTo(Route.Home.path) },
                        onLogin = { navigateTo(Route.Login.path) },
                        onRegister = { navigateTo(Route.Register.path) }
                    )
                }
            },
            bottomBar = {
                // SOLO se muestra en Home y HomeAdmin
                if (mostrarBottomBar) {
                    AppBottomBarV2(
                        currentRoute = currentRoute,
                        onNavigate = navigateTo,
                        onOpenDrawer = openDrawer, // Ahora es una función válida
                        onProfile = {
                            navigateTo("perfil")
                        },
                        onLogout = {
                            usuarioViewModel.cerrarSesion()
                            navigateTo(Route.Login.path)
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Login.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Login.path) {
                    LoginScreen(navController, usuarioViewModel) // Pasar el ViewModel
                }
                composable(Route.Register.path) { RegistroScreen(navController) }
                composable(Route.Home.path) {
                    HomeScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel, // Pasar el ViewModel
                        onHome = { navigateTo(Route.Home.path) },
                        onLogin = { navigateTo(Route.Login.path) },
                        onRegister = { navigateTo(Route.Register.path) }
                    )
                }
                composable(Route.HomeAdmin.path) {
                    HomeAdminScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel, // Pasar el ViewModel
                        onHome = { navigateTo(Route.Home.path) },
                        onLogin = { navigateTo(Route.Login.path) },
                        onRegister = { navigateTo(Route.Register.path) }
                    )
                }
                composable("perfil") {
                    PerfilScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel, // Pasar el ViewModel
                        onHome = { navigateTo(Route.Home.path) }
                    )
                }
                composable(Route.Productos.path) { ProductosScreen(navController) }
                composable(Route.ListaProductos.path) { ListaProductosScreen(navController) }
                composable(Route.FormularioProducto.path) { FormularioProductoScreen(navController) }
                composable(Route.Categoria.path) { CategoriaScreen(navController) }
                composable(Route.ListaCategoria.path) { ListaCategoriaScreen(navController) }
                composable(Route.FormularioCategoria.path) { FormularioCategoriaScreen(navController) }
                composable(Route.FormularioProducto.path) { FormularioProductoScreen(navController) }
                composable(Route.Entradas_y_Salidas_Productos.path) { Entradas_y_Salidas_ProductosScreen(navController) }
                composable(Route.Entradas.path) { EntradasScreen(navController) }
                composable(Route.Salidas.path) { SalidasScreen(navController) }
                composable(Route.Proveedores.path) { ProveedoresScreen(navController) }
                composable(Route.FormularioProveedores.path) { FormularioProveedoresScreen(navController) }
                composable(Route.ListaProveedores.path) { ListaProveedoresScreen(navController) }
                composable(Route.Usuario.path) { UsuarioScreen(navController) }
            }
        }
    }
}