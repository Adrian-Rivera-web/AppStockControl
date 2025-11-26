package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appstockcontrol_grupo_07.data.repository.MovimientoInventarioRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.model.MovimientoInventario
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first   // 👈 IMPORTANTE: para leer el Flow una vez

data class MovimientosUiState(
    val movimientos: List<MovimientoInventario> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class MovimientoInventarioViewModel(
    private val movimientoRepo: MovimientoInventarioRepository,
    private val productoRepo: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovimientosUiState())
    val uiState: StateFlow<MovimientosUiState> = _uiState

    fun cargarMovimientos() {
        viewModelScope.launch {
            movimientoRepo.obtenerMovimientos().collect { lista ->
                _uiState.value = MovimientosUiState(movimientos = lista)
            }
        }
    }

    fun cargarKardexProducto(productoId: Int) {
        viewModelScope.launch {
            movimientoRepo.obtenerMovimientosPorProducto(productoId).collect { lista ->
                _uiState.value = MovimientosUiState(movimientos = lista)
            }
        }
    }

    fun registrarEntrada(producto: Producto, cantidad: Int, usuario: String, motivo: String = "") {
        if (cantidad <= 0) return

        viewModelScope.launch {
            // ✅ Obtener SIEMPRE el producto ACTUAL desde el Flow de productos
            val listaProductos = productoRepo.obtenerProductos().first()
            val productoActual = listaProductos.find { it.id == producto.id } ?: producto

            val stockAnterior = productoActual.stock
            val stockNuevo = stockAnterior + cantidad

            // 1) actualizar producto con el stock nuevo
            val actualizado = productoActual.copy(stock = stockNuevo)
            productoRepo.actualizarProducto(actualizado)

            // 2) registrar movimiento
            val mov = MovimientoInventario(
                productoId = productoActual.id,
                tipo = TipoMovimiento.ENTRADA,
                cantidad = cantidad,
                usuario = usuario,
                motivo = motivo,
                stockAnterior = stockAnterior,
                stockNuevo = stockNuevo
            )
            movimientoRepo.registrarMovimiento(mov)
        }
    }

    fun registrarSalida(producto: Producto, cantidad: Int, usuario: String, motivo: String = "") {
        if (cantidad <= 0) return

        viewModelScope.launch {
            // ✅ Igual que en entrada: leer producto actualizado desde el Flow
            val listaProductos = productoRepo.obtenerProductos().first()
            val productoActual = listaProductos.find { it.id == producto.id } ?: producto

            val stockAnterior = productoActual.stock

            // ⛔ Seguridad extra: no permitir sacar más que el stock actual
            if (cantidad > stockAnterior) {
                return@launch
            }

            val stockNuevo = stockAnterior - cantidad

            val actualizado = productoActual.copy(stock = stockNuevo)
            productoRepo.actualizarProducto(actualizado)

            val mov = MovimientoInventario(
                productoId = productoActual.id,
                tipo = TipoMovimiento.SALIDA,
                cantidad = cantidad,
                usuario = usuario,
                motivo = motivo,
                stockAnterior = stockAnterior,
                stockNuevo = stockNuevo
            )
            movimientoRepo.registrarMovimiento(mov)
        }
    }
}
