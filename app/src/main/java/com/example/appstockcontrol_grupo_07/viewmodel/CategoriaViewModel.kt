package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.model.Categoria

class CategoriaViewModel(
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaState())
    val uiState: StateFlow<CategoriaState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                categoriaRepository.obtenerCategorias().collect { categorias ->
                    _uiState.update {
                        it.copy(
                            categorias = categorias,
                            cargando = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar categorías: ${e.message}",
                        cargando = false
                    )
                }
            }
        }
    }

    fun buscarCategorias(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                cargarCategorias()
            } else {
                _uiState.update { it.copy(buscando = true) }
                try {
                    categoriaRepository.buscarCategorias(query).collect { resultados ->
                        _uiState.update {
                            it.copy(
                                categorias = resultados,
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

    fun eliminarCategoria(id: Int) {
        viewModelScope.launch {
            try {
                categoriaRepository.eliminarCategoria(id)
                // Recargar la lista después de eliminar
                cargarCategorias()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al eliminar categoría: ${e.message}"
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
        cargarCategorias()
    }
}

data class CategoriaState(
    val categorias: List<Categoria> = emptyList(),
    val cargando: Boolean = false,
    val buscando: Boolean = false,
    val busquedaActual: String = "",
    val error: String? = null
)