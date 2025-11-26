package com.example.appstockcontrol_grupo_07.data.local.producto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
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