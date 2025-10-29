package com.example.appstockcontrol_grupo_07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.appstockcontrol_grupo_07.data.local.database.AppDatabase
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.navigation.AppNavGraph
import com.example.appstockcontrol_grupo_07.ui.theme.AppStockControl_Grupo_07Theme
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModel
import com.example.appstockcontrol_grupo_07.viewmodel.AdminViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.CategoriaViewModelFactory
import com.example.appstockcontrol_grupo_07.viewmodel.FormularioCategoriaViewModelFactory
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

    private val categoriaRepository by lazy {
        CategoriaRepository(database.categoriaDao())
    }

    // Factories para ViewModels
    private val productoViewModelFactory by lazy {
        ProductoViewModelFactory(productoRepository)
    }

    private val adminViewModelFactory by lazy {
        AdminViewModelFactory(userRepository)
    }

    private val categoriaViewModelFactory by lazy {
        CategoriaViewModelFactory(categoriaRepository)
    }

    private val formularioCategoriaViewModelFactory by lazy {
        FormularioCategoriaViewModelFactory(categoriaRepository)
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
                    AppNavGraph(
                        usuarioViewModel = usuarioViewModel,
                        productoViewModel = productoViewModel,
                        adminViewModel = adminViewModel,
                        categoriaViewModelFactory = categoriaViewModelFactory,
                        formularioCategoriaViewModelFactory = formularioCategoriaViewModelFactory
                    )
                }
            }
        }
    }
}