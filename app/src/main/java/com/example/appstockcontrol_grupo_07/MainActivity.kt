package com.example.appstockcontrol_grupo_07
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.appstockcontrol_grupo_07.navigation.AppNavigation
import com.example.appstockcontrol_grupo_07.ui.theme.AppStockControl_Grupo_07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppStockControl_Grupo_07Theme {
                Scaffold { innerPadding ->
                    // 🔥 Aquí insertamos el flujo de navegación en lugar de Greeting()
                    Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
