package com.example.appstockcontrol_grupo_07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.navigation.AppNavGraph
import com.example.appstockcontrol_grupo_07.ui.theme.AppStockControl_Grupo_07Theme
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.ProductoViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.ProductoViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    // Obtener la base de datos usando el método getInstance que ya tienes
    private val database by lazy {
        AppDatabase.getInstance(this)
    }

    // Repositorios
    private val userRepository by lazy {
        UserRepository(database.userDao())
    }

    private val productoRepository by lazy {
        ProductoRepository(database.productoDao())
    }

    // Factories para ViewModels
    private val productoViewModelFactory by lazy {
        ProductoViewModelFactory(productoRepository)
    }

    private val adminViewModelFactory by lazy {
        AdminViewModelFactory(userRepository)
    }

    // ViewModels
    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val productoViewModel: ProductoViewModel by viewModels { productoViewModelFactory }
    private val adminViewModel: AdminViewModel by viewModels { adminViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppStockControl_Grupo_07Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pasar todos los ViewModels al NavGraph
                    AppNavGraph(
                        usuarioViewModel = usuarioViewModel,
                        productoViewModel = productoViewModel,
                        adminViewModel = adminViewModel
                    )
                }
            }
        }
    }
}