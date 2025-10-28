package com.example.appstockcontrol_grupo_07.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AzulVibrante,
    secondary = NaranjaModerno,
    tertiary = Pink80,
    background = GrisOscuro,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GrisMuyClaro,
    onSurface = GrisMuyClaro
)

private val LightColorScheme = lightColorScheme(
    primary = AzulVibrante,
    secondary = NaranjaModerno,
    tertiary = Pink40,
    background = GrisMuyClaro,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GrisOscuro,
    onSurface = GrisOscuro
)

@Composable
fun AppStockControl_Grupo_07Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
