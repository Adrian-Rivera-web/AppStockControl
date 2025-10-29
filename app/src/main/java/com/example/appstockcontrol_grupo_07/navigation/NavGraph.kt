package com.example.appstockcontrol_grupo_07.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.example.appstockcontrol_grupo_07.viewmodel.ProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModel
import com.example.appstockcontrol_grupo_07.ui.screen.CategoriaScreen
import com.example.appstockcontrol_grupo_07.ui.screen.EntradasScreen
import com.example.appstockcontrol_grupo_07.ui.screen.Entradas_y_Salidas_ProductosScreen
import com.example.appstockcontrol_grupo_07.ui.screen.FormularioCategoriaScreen
import com.example.appstockcontrol_grupo_07.ui.screen.FormularioProductoScreen
import com.example.appstockcontrol_grupo_07.ui.screen.FormularioProveedoresScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ListaCategoriaScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ListaProductosScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ListaProveedoresScreen
import com.example.appstockcontrol_grupo_07.ui.screen.ProveedoresScreen
import com.example.appstockcontrol_grupo_07.ui.screen.SalidasScreen
import com.example.appstockcontrol_grupo_07.ui.screen.UsuarioScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    usuarioViewModel: UsuarioViewModel,
    productoViewModel: ProductoViewModel,
    adminViewModel: AdminViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Estado del drawer y coroutine scope
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Obtener el estado del usuario para determinar startDestination y navegación
    val usuarioLogueado by usuarioViewModel.usuarioLogueado.collectAsState()
    val esAdmin by usuarioViewModel.esAdmin.collectAsState()

    // Determinar startDestination dinámicamente
    val startDestination = if (usuarioLogueado != null) {
        if (esAdmin) Route.HomeAdmin.path else Route.Home.path
    } else {
        Route.Login.path
    }

    // Función helper para navegación
    val navigateTo: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    // Determinar cuándo mostrar las barras - SOLO en pantallas principales
    val mostrarTopBar = when (currentRoute) {
        Route.Home.path, Route.HomeAdmin.path -> true
        else -> false
    }

    val mostrarBottomBar = when (currentRoute) {
        Route.Home.path, Route.HomeAdmin.path -> true
        else -> false
    }

    // Función para abrir el drawer
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
            // Navegar al home correcto según el rol
            val homeRoute = if (esAdmin) Route.HomeAdmin.path else Route.Home.path
            navigateTo(homeRoute)
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
        gesturesEnabled = mostrarBottomBar, // Solo permitir gestos donde hay bottom bar
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
                if (mostrarTopBar) {
                    AppTopBar(
                        onOpenDrawer = openDrawer,
                        onSettings = {
                            // ✅ Navegar a pantalla de configuración (puedes crear esta pantalla después)
                            // Por ahora puedes dejarlo vacío o navegar a un placeholder
                            println("Navegar a pantalla de configuración")
                        },
                        onLogout = {
                            println("DEBUG: AppNavGraph - Cerrando sesión desde TopBar")
                            usuarioViewModel.cerrarSesion()
                            navController.navigate(Route.Login.path) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (mostrarBottomBar) {
                    AppBottomBarV2(
                        currentRoute = currentRoute,
                        onNavigate = navigateTo,
                        isAdmin = esAdmin, // ✅ Pasar el estado de admin
                        onProfile = {
                            navigateTo("perfil")
                        },
                        onLogout = {
                            println("DEBUG: AppNavGraph - Cerrando sesión desde BottomBar")
                            usuarioViewModel.cerrarSesion()
                            navController.navigate(Route.Login.path) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination, // ✅ Start dinámico
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Login.path) {
                    LoginScreen(navController, usuarioViewModel)
                }
                composable(Route.Register.path) {
                    RegistroScreen(navController)
                }
                composable(Route.Home.path) {
                    HomeScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel
                        // ❌ Quitamos onHome, onLogin, onRegister - ya no son necesarios
                    )
                }
                composable(Route.HomeAdmin.path) {
                    HomeAdminScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        productoViewModel = productoViewModel,
                        adminViewModel = adminViewModel
                        // ❌ Quitamos onHome, onLogin, onRegister - ya no son necesarios
                    )
                }
                composable("perfil") {
                    PerfilScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel
                        // ❌ Quitamos onHome - ya no es necesario
                    )
                }
                composable(Route.ListaProductos.path) {
                    ListaProductosScreen(navController)
                }

                composable(
                    route = "${Route.FormularioProducto.path}?productoId={productoId}",
                    arguments = listOf(
                        navArgument("productoId") {
                            type = NavType.StringType
                            defaultValue = "0"
                        }
                    )
                ) { backStackEntry ->
                    val productoId = backStackEntry.arguments?.getString("productoId")
                    FormularioProductoScreen(navController, productoId)
                }
                composable(Route.Categoria.path) {
                    CategoriaScreen(navController)
                }
                composable(Route.ListaCategoria.path) {
                    ListaCategoriaScreen(navController)
                }
                composable(Route.FormularioCategoria.path) {
                    FormularioCategoriaScreen(navController)
                }
                composable(Route.Entradas_y_Salidas_Productos.path) {
                    Entradas_y_Salidas_ProductosScreen(navController)
                }
                composable(Route.Entradas.path) {
                    EntradasScreen(navController)
                }
                composable(Route.Salidas.path) {
                    SalidasScreen(navController)
                }
                composable(Route.Proveedores.path) {
                    ProveedoresScreen(navController)
                }
                composable(Route.FormularioProveedores.path) {
                    FormularioProveedoresScreen(navController)
                }
                composable(Route.ListaProveedores.path) {
                    ListaProveedoresScreen(navController)
                }
                composable(Route.Usuario.path) {
                    UsuarioScreen(navController, usuarioViewModel)
                }
            }
        }
    }
}