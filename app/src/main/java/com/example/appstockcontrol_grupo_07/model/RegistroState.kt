package com.example.appstockcontrol_grupo_07.model
data class RegistroState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "", // ← Agregar
    val claveConfirmacion: String = "", // Nuevo campo
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: RegistroErrores = RegistroErrores()
)