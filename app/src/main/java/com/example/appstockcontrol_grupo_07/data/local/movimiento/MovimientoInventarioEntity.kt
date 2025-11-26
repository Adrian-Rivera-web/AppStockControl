package com.example.appstockcontrol_grupo_07.data.local.movimiento

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos_inventario")
data class MovimientoInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val tipo: String,         // guardamos el enum como String (nombre)
    val cantidad: Int,
    val fecha: Long,
    val usuario: String,
    val motivo: String,
    val stockAnterior: Int,
    val stockNuevo: Int
)
