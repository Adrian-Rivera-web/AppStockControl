package com.example.appstockcontrol_grupo_07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.appstockcontrol_grupo_07.navigation.AppNavGraph
import com.example.appstockcontrol_grupo_07.ui.theme.AppStockControl_Grupo_07Theme
import com.example.appstockcontrol_grupo_07.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    // Obtener el ViewModel a nivel de actividad
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppStockControl_Grupo_07Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pasar el ViewModel al NavGraph
                    AppNavGraph(usuarioViewModel = usuarioViewModel)
                }
            }
        }
    }
}
