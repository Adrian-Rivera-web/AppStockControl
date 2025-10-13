package com.example.appstockcontrol_grupo_07.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appstockcontrol_grupo_07.ui.screen.RegistroScreen
import com.example.appstockcontrol_grupo_07.ui.screen.HomeScreen
import com.example.appstockcontrol_grupo_07.ui.screen.LoginScreen
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 🔥 Aquí creamos el ViewModel una sola vez
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            LoginScreen(navController,usuarioViewModel)
        }
        composable(route = "registro") {
            RegistroScreen(navController,usuarioViewModel)
        }
        composable(route = "home") {
            HomeScreen(navController,usuarioViewModel)
        }
    }
}
