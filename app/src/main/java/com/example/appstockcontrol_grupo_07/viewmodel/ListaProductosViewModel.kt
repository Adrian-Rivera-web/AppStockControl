// ListaProductosViewModel.kt
package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository

class ListaProductosViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaProductosState())
    val uiState: StateFlow<ListaProductosState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                productoRepository.obtenerProductos().collect { productos ->
                    _uiState.update {
                        it.copy(
                            productos = productos,
                            cargando = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar productos: ${e.message}",
                        cargando = false
                    )
                }
            }
        }
    }

    fun buscarProductos(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                cargarProductos()
            } else {
                _uiState.update { it.copy(buscando = true) }
                try {
                    productoRepository.buscarProductos(query).collect { resultados ->
                        _uiState.update {
                            it.copy(
                                productos = resultados,
                                busquedaActual = query,
                                buscando = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = "Error en búsqueda: ${e.message}",
                            buscando = false
                        )
                    }
                }
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            try {
                productoRepository.eliminarProducto(id)
                // Recargar la lista después de eliminar
                cargarProductos()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al eliminar producto: ${e.message}"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }

    fun limpiarBusqueda() {
        _uiState.update { it.copy(busquedaActual = "") }
        cargarProductos()
    }
}

data class ListaProductosState(
    val productos: List<Producto> = emptyList(),
    val cargando: Boolean = false,
    val buscando: Boolean = false,
    val busquedaActual: String = "",
    val error: String? = null
)