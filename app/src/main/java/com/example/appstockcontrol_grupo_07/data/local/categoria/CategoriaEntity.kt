package com.example.appstockcontrol_grupo_07.data.local.categoria

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val fechaCreacion: Long,
    val activa: Int // 1 para true, 0 para false
)