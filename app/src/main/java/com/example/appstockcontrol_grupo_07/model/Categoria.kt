// Categoria.kt
package com.example.appstockcontrol_grupo_07.model

data class Categoria(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val fechaCreacion: Long,
    val activa: Boolean
)