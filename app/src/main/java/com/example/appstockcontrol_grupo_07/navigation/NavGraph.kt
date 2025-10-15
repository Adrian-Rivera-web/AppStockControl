package com.example.appstockcontrol_grupo_07.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appstockcontrol_grupo_07.ui.components.AppDrawer
import com.example.appstockcontrol_grupo_07.ui.components.AppTopBar
import com.example.appstockcontrol_grupo_07.ui.components.defaultDrawerItems
import com.example.appstockcontrol_grupo_07.ui.screen.HomeScreen
import com.example.appstockcontrol_grupo_07.ui.screen.LoginScreen
import com.example.appstockcontrol_grupo_07.ui.screen.RegistroScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Helpers de navegación
    val goHome = { navController.navigate(Route.Home.path) }
    val goLogin = { navController.navigate(Route.Login.path) }
    val goRegister = { navController.navigate(Route.Register.path) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onLogin = { scope.launch { drawerState.close() }; goLogin() },
                    onRegister = { scope.launch { drawerState.close() }; goRegister() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Login.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Login.path) { LoginScreen(navController, usuarioViewModel) }
                composable(Route.Register.path) { RegistroScreen(navController, usuarioViewModel) }
                composable(Route.Home.path) { HomeScreen(navController, usuarioViewModel) }
            }
        }
    }
}
