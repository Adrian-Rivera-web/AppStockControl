// Producto.kt
package com.example.appstockcontrol_grupo_07.model

data class Producto(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int = 0,
    val categoria: String,
    val proveedor: String,
    val fechaCreacion: String = "",
    val fechaActualizacion: String = "",
    val imagenUri: String? = null
)