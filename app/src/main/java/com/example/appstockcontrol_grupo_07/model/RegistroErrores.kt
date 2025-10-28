package com.example.appstockcontrol_grupo_07.model

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null, // ← Agregar
    val clave: String? = null,
    val claveConfirmacion: String? = null,
    val direccion: String? = null
)