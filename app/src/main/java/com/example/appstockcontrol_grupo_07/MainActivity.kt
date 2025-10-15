package com.example.appstockcontrol_grupo_07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appstockcontrol_grupo_07.ui.theme.AppStockControl_Grupo_07Theme
import com.example.appstockcontrol_grupo_07.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppStockControl_Grupo_07Theme {
                AppNavGraph() // ✅ Aquí va la nueva navegación
            }
        }
    }
}
