package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.movimiento.MovimientoInventarioDao
import com.example.appstockcontrol_grupo_07.data.local.movimiento.MovimientoInventarioEntity
import com.example.appstockcontrol_grupo_07.model.MovimientoInventario
import com.example.appstockcontrol_grupo_07.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovimientoInventarioRepository(
    private val movimientoDao: MovimientoInventarioDao
) {

    fun obtenerMovimientos(): Flow<List<MovimientoInventario>> =
        movimientoDao.obtenerTodos().map { entities ->
            entities.map { it.toModel() }
        }

    fun obtenerMovimientosPorProducto(productoId: Int): Flow<List<MovimientoInventario>> =
        movimientoDao.obtenerPorProducto(productoId).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun registrarMovimiento(movimiento: MovimientoInventario) {
        movimientoDao.insertar(movimiento.toEntity())
    }
}

// 🔁 Conversión

private fun MovimientoInventarioEntity.toModel(): MovimientoInventario =
    MovimientoInventario(
        id = id,
        productoId = productoId,
        tipo = TipoMovimiento.valueOf(tipo),
        cantidad = cantidad,
        fecha = fecha,
        usuario = usuario,
        motivo = motivo,
        stockAnterior = stockAnterior,
        stockNuevo = stockNuevo
    )

private fun MovimientoInventario.toEntity(): MovimientoInventarioEntity =
    MovimientoInventarioEntity(
        id = id,
        productoId = productoId,
        tipo = tipo.name,
        cantidad = cantidad,
        fecha = fecha,
        usuario = usuario,
        motivo = motivo,
        stockAnterior = stockAnterior,
        stockNuevo = stockNuevo
    )
