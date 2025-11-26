package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ReportesInventarioUiState(
    val totalProductos: Int = 0,
    val productosSinStock: Int = 0,
    val productosBajoMinimo: Int = 0,
    val totalUnidades: Int = 0,
    val valorTotalInventario: Double = 0.0,
    val topProductosBajoStock: List<Producto> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class ReportesInventarioViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesInventarioUiState())
    val uiState: StateFlow<ReportesInventarioUiState> = _uiState

    init {
        cargarReportes()
    }

    private fun cargarReportes() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(cargando = true, error = null)

                // ⚠️ Ajusta el nombre si tu repo se llama distinto (ej: getProductos(), obtenerTodos(), etc.)
                productoRepository.obtenerProductos()
                    .collectLatest { productos ->
                        _uiState.value = calcularReportes(productos)
                    }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "Error al cargar reportes: ${e.message}"
                )
            }
        }
    }

    private fun calcularReportes(productos: List<Producto>): ReportesInventarioUiState {
        if (productos.isEmpty()) {
            return ReportesInventarioUiState(
                cargando = false,
                error = null
            )
        }

        val totalProductos = productos.size

        val productosSinStock = productos.count { it.stock <= 0 }

        val productosBajoMinimo = productos.count { prod ->
            prod.stockMinimo > 0 && prod.stock in 1..prod.stockMinimo
        }

        val totalUnidades = productos.sumOf { it.stock.coerceAtLeast(0) }

        val valorTotalInventario = productos.sumOf { prod ->
            (prod.precio * prod.stock.coerceAtLeast(0))
        }

        val topProductosBajoStock = productos
            .filter { it.stockMinimo > 0 }
            .sortedBy { it.stock - it.stockMinimo } // más críticos arriba
            .take(5)

        return ReportesInventarioUiState(
            totalProductos = totalProductos,
            productosSinStock = productosSinStock,
            productosBajoMinimo = productosBajoMinimo,
            totalUnidades = totalUnidades,
            valorTotalInventario = valorTotalInventario,
            topProductosBajoStock = topProductosBajoStock,
            cargando = false,
            error = null
        )
    }
}

class ReportesInventarioViewModelFactory(
    private val productoRepository: ProductoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportesInventarioViewModel::class.java)) {
            return ReportesInventarioViewModel(productoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
