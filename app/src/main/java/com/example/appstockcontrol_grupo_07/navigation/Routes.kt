package com.example.appstockcontrol_grupo_07.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("registro")
}
