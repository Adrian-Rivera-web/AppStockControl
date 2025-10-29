package com.example.appstockcontrol_grupo_07.data.local.proveedor

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proveedores")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val contacto: String,
    val telefono: String,
    val email: String,
    val direccion: String,
    val activo: Int
)