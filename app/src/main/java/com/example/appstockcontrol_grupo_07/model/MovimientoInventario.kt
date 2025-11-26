package com.example.appstockcontrol_grupo_07.model

enum class TipoMovimiento {
    ENTRADA,
    SALIDA,
    AJUSTE   // opcional: para conteos / correcciones
}

data class MovimientoInventario(
    val id: Int = 0,
    val productoId: Int,              // Id del producto al que afecta
    val tipo: TipoMovimiento,         // ENTRADA | SALIDA | AJUSTE
    val cantidad: Int,                // Unidades movidas (siempre positivas)
    val fecha: Long = System.currentTimeMillis(), // Fecha en millis
    val usuario: String,              // quién hizo el movimiento (correo o nombre)
    val motivo: String = "",          // descripción / comentario
    val stockAnterior: Int,           // stock antes del movimiento
    val stockNuevo: Int               // stock después del movimiento
)
