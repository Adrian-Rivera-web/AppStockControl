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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.appstockcontrol_grupo_07.ui.components.adminDrawerItems
import com.example.appstockcontrol_grupo_07.ui.components.userDrawerItems
import com.example.appstockcontrol_grupo_07.ui.components.defaultDrawerItems
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProveedorRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.ui.components.AppBottomBarV2
import com.example.appstockcontrol_grupo_07.ui.components.AppDrawer
import com.example.appstockcontrol_grupo_07.ui.components.AppTopBar
import com.example.appstockcontrol_grupo_07.ui.components.defaultDrawerItems
import com.example.appstockcontrol_grupo_07.ui.screen.*
import com.example.appstockcontrol_grupo_07.viewmodel.*
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.viewmodel.CategoriaViewModel

@Composable
fun AppNavGraph(
    usuarioViewModel: UsuarioViewModel,
    productoViewModel: ProductoViewModel,
    adminViewModel: AdminViewModel,
    categoriaViewModelFactory: CategoriaViewModelFactory,
    formularioCategoriaViewModelFactory: FormularioCategoriaViewModelFactory
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
    // Lista de ítems para el drawer - DIFERENCIADA POR ROL
    val drawerItems = if (usuarioLogueado != null) {
        if (esAdmin) {
            adminDrawerItems(
                onHome = {
                    navigateTo(Route.HomeAdmin.path)
                    closeDrawer()
                },
                onListaUsuarios = {
                    navigateTo(Route.Usuario.path)
                    closeDrawer()
                },
                onListaProductos = {
                    navigateTo(Route.ListaProductos.path)
                    closeDrawer()
                },
                onFormularioProducto = {
                    navigateTo(Route.FormularioProducto.path)
                    closeDrawer()
                },
                onListaCategorias = {
                    navigateTo(Route.ListaCategoria.path)
                    closeDrawer()
                },
                onFormularioCategoria = {
                    navigateTo(Route.FormularioCategoria.path)
                    closeDrawer()
                },
                onListaProveedores = {
                    navigateTo(Route.ListaProveedores.path)
                    closeDrawer()
                },
                onFormularioProveedores = {
                    navigateTo(Route.FormularioProveedores.path)
                    closeDrawer()
                }
            )
        } else {
            userDrawerItems(
                onHome = {
                    navigateTo(Route.Home.path)
                    closeDrawer()
                },
                onListaProductos = {
                    navigateTo(Route.ListaProductos.path)
                    closeDrawer()
                },
                onPerfil = {
                    navigateTo("perfil") // Asegúrate de que la ruta para perfil esté definida en Route o sea la correcta
                    closeDrawer()
                },
                onListaCategorias = {
                    navigateTo(Route.ListaCategoria.path)
                    closeDrawer()
                },
                onListaProveedores = {
                    navigateTo(Route.ListaProveedores.path)
                    closeDrawer()
                },
                onEntradasSalidas = {
                    navigateTo(Route.Entradas_y_Salidas_Productos.path)
                    closeDrawer()
                }
            )
        }
    } else {
        defaultDrawerItems(
            onHome = {
                navigateTo(Route.Login.path) // Redirigir a login si no está logueado
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
    }

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
                if (mostrarTopBar) {
                    AppTopBar(
                        onOpenDrawer = openDrawer,
                        onSettings = {
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
                        isAdmin = esAdmin,
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
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Login.path) {
                    LoginScreen(navController, usuarioViewModel)
                }
                composable(Route.Register.path) {
                    RegistroScreen(navController)
                }
                composable(Route.Home.path) {

                    val context = LocalContext.current
                    val database = AppDatabase.getInstance(context)

                    // Repositorios
                    val categoriaRepository = CategoriaRepository(database.categoriaDao())
                    val proveedorRepository = ProveedorRepository(database.proveedorDao())

                    // ViewModels
                    val categoriaViewModel: CategoriaViewModel = viewModel(
                        factory = CategoriaViewModelFactory(categoriaRepository)
                    )
                    val proveedorViewModel: ProveedorViewModel = viewModel(
                        factory = ProveedorViewModelFactory(proveedorRepository)
                    )

                    HomeScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        productoViewModel = productoViewModel,
                        categoriaViewModel = categoriaViewModel,
                        proveedorViewModel = proveedorViewModel
                    )
                }
                composable(Route.HomeAdmin.path) {
                    val context = LocalContext.current
                    val database = AppDatabase.getInstance(context)
                    val categoriaRepository = CategoriaRepository(database.categoriaDao())
                    val proveedorRepository = ProveedorRepository(database.proveedorDao())
                    val categoriaViewModel: CategoriaViewModel = viewModel(
                        factory = CategoriaViewModelFactory(categoriaRepository)
                    )
                    val proveedorViewModel: ProveedorViewModel = viewModel(
                        factory = ProveedorViewModelFactory(proveedorRepository)
                    )
                    HomeAdminScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        productoViewModel = productoViewModel,
                        adminViewModel = adminViewModel,
                        categoriaViewModel = categoriaViewModel,
                        proveedorViewModel = proveedorViewModel
                    )
                }
                composable("perfil") {
                    PerfilScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel
                    )
                }

                composable(
                    "listaProductos?esAdmin={esAdmin}",
                    arguments = listOf(
                        navArgument("esAdmin") {
                            type = NavType.BoolType
                            defaultValue = true
                        }
                    )
                ) { backStackEntry ->
                    val esAdmin = backStackEntry.arguments?.getBoolean("esAdmin") ?: true

                    ListaProductosScreen(
                        navController = navController,
                        esAdmin = esAdmin
                    )
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

                composable(Route.ListaCategoria.path) {
                    val context = LocalContext.current
                    val database = AppDatabase.getInstance(context)
                    val categoriaRepository = CategoriaRepository(database.categoriaDao())
                    val categoriaViewModel: CategoriaViewModel = viewModel(
                        factory = CategoriaViewModelFactory(categoriaRepository)
                    )
                    ListaCategoriasScreen(
                        navController = navController,
                        categoriaViewModel = categoriaViewModel
                    )
                }

                composable(
                    route = "${Route.FormularioCategoria.path}?categoriaId={categoriaId}",
                    arguments = listOf(
                        navArgument("categoriaId") {
                            type = NavType.StringType
                            defaultValue = "0"
                        }
                    )
                ) { backStackEntry ->
                    val context = LocalContext.current
                    val database = AppDatabase.getInstance(context)
                    val categoriaRepository = CategoriaRepository(database.categoriaDao())
                    val formularioCategoriaViewModel: FormularioCategoriaViewModel = viewModel(
                        factory = FormularioCategoriaViewModelFactory(categoriaRepository)
                    )
                    val categoriaId = backStackEntry.arguments?.getString("categoriaId")
                    FormularioCategoriaScreen(
                        navController = navController,
                        categoriaId = categoriaId,
                        viewModel = formularioCategoriaViewModel
                    )
                }
                composable(Route.Categoria.path) {
                    // CategoriaScreen si existe
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