package com.example.appstockcontrol_grupo_07.model

data class Proveedor(
    val id: Int = 0,
    val nombre: String,
    val contacto: String,
    val telefono: String,
    val email: String,
    val direccion: String,
    val activo: Boolean = true
)