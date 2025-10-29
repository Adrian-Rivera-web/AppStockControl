package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.ProveedorRepository
import com.example.appstockcontrol_grupo_07.model.Proveedor

class ProveedorViewModel(
    private val proveedorRepository: ProveedorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProveedorState())
    val uiState: StateFlow<ProveedorState> = _uiState.asStateFlow()

    init {
        cargarProveedores()
    }

    fun cargarProveedores() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                proveedorRepository.obtenerProveedores().collect { proveedores ->
                    _uiState.update {
                        it.copy(
                            proveedores = proveedores,
                            cargando = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar proveedores: ${e.message}",
                        cargando = false
                    )
                }
            }
        }
    }

    fun buscarProveedores(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                cargarProveedores()
            } else {
                _uiState.update { it.copy(buscando = true) }
                try {
                    proveedorRepository.buscarProveedores(query).collect { resultados ->
                        _uiState.update {
                            it.copy(
                                proveedores = resultados,
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

    fun eliminarProveedor(id: Int) {
        viewModelScope.launch {
            try {
                proveedorRepository.eliminarProveedor(id)
                // Recargar la lista después de eliminar
                cargarProveedores()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al eliminar proveedor: ${e.message}"
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
        cargarProveedores()
    }
}

data class ProveedorState(
    val proveedores: List<Proveedor> = emptyList(),
    val cargando: Boolean = false,
    val buscando: Boolean = false,
    val busquedaActual: String = "",
    val error: String? = null
)