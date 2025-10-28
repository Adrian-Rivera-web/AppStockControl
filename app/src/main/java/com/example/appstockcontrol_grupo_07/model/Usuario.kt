package com.example.appstockcontrol_grupo_07.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val esAdmin: Boolean = false
)