package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.movimiento.MovimientoInventarioDao
import com.example.appstockcontrol_grupo_07.data.local.movimiento.MovimientoInventarioEntity
import com.example.appstockcontrol_grupo_07.model.MovimientoInventario
import com.example.appstockcontrol_grupo_07.model.TipoMovimiento
import com.example.appstockcontrol_grupo_07.remote.MovimientoApi
import com.example.appstockcontrol_grupo_07.remote.MovimientoRemotoDto
import com.example.appstockcontrol_grupo_07.remote.RegistrarEntradaRemoteRequest
import com.example.appstockcontrol_grupo_07.remote.RegistrarSalidaRemoteRequest
import com.example.appstockcontrol_grupo_07.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovimientoInventarioRepository(
    // Lo dejamos opcional, por si más adelante quieres cache local con Room
    private val movimientoDao: MovimientoInventarioDao? = null,
    private val api: MovimientoApi = RetrofitInstance.movimientosApi
) {

    // ========= LISTAR TODOS LOS MOVIMIENTOS =========
    fun obtenerMovimientos(): Flow<List<MovimientoInventario>> = flow {
        val remotos = api.listarMovimientos()
        emit(remotos.map { it.toModel() })
    }

    // ========= LISTAR MOVIMIENTOS POR PRODUCTO =========
    fun obtenerMovimientosPorProducto(productoId: Int): Flow<List<MovimientoInventario>> = flow {
        val remotos = api.listarMovimientosPorProducto(productoId.toLong())
        emit(remotos.map { it.toModel() })
    }

    // ========= REGISTRAR MOVIMIENTO (entrada / salida / ajuste) =========
    suspend fun registrarMovimiento(mov: MovimientoInventario) {
        when (mov.tipo) {
            TipoMovimiento.ENTRADA -> {
                val req = RegistrarEntradaRemoteRequest(
                    productoId = mov.productoId.toLong(),
                    cantidad = mov.cantidad,
                    usuarioResponsable = mov.usuario,
                    motivo = mov.motivo
                )
                api.registrarEntrada(req, mov.stockAnterior, mov.stockNuevo)
            }

            TipoMovimiento.SALIDA -> {
                val req = RegistrarSalidaRemoteRequest(
                    productoId = mov.productoId.toLong(),
                    cantidad = mov.cantidad,
                    usuarioResponsable = mov.usuario,
                    motivo = mov.motivo
                )
                api.registrarSalida(req, mov.stockAnterior, mov.stockNuevo)
            }

            TipoMovimiento.AJUSTE -> {
                // 👀 Para AJUSTE decidimos si es entrada o salida según stock
                val esEntrada = mov.stockNuevo >= mov.stockAnterior
                val cantidadAjuste = kotlin.math.abs(mov.cantidad)

                if (esEntrada) {
                    val req = RegistrarEntradaRemoteRequest(
                        productoId = mov.productoId.toLong(),
                        cantidad = cantidadAjuste,
                        usuarioResponsable = mov.usuario,
                        motivo = "[AJUSTE] ${mov.motivo}"
                    )
                    api.registrarEntrada(req, mov.stockAnterior, mov.stockNuevo)
                } else {
                    val req = RegistrarSalidaRemoteRequest(
                        productoId = mov.productoId.toLong(),
                        cantidad = cantidadAjuste,
                        usuarioResponsable = mov.usuario,
                        motivo = "[AJUSTE] ${mov.motivo}"
                    )
                    api.registrarSalida(req, mov.stockAnterior, mov.stockNuevo)
                }
            }
        }

        // (Opcional) también podrías guardar localmente en Room:
        // movimientoDao?.insert(mov.toEntity())
    }

    // ========= MAPEOS (puedes aprovecharlos si activas Room más adelante) =========

    private fun MovimientoRemotoDto.toModel(): MovimientoInventario =
        MovimientoInventario(
            id = this.id?.toInt() ?: 0,
            productoId = this.productoId?.toInt() ?: 0,
            tipo = when (this.tipo) {
                "ENTRADA" -> TipoMovimiento.ENTRADA
                "SALIDA" -> TipoMovimiento.SALIDA
                else -> TipoMovimiento.ENTRADA
            },
            cantidad = this.cantidad ?: 0,
            // El backend ya ordena por fechaHora, aquí solo mostramos "ahora" como fecha
            // Si quisieras, podrías parsear fechaHora → millis, pero no es obligatorio para la demo
            fecha = System.currentTimeMillis(),
            usuario = this.usuarioResponsable ?: "",
            motivo = this.motivo ?: "",
            stockAnterior = this.stockAnterior ?: 0,
            stockNuevo = this.stockNuevo ?: 0
        )

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
}

