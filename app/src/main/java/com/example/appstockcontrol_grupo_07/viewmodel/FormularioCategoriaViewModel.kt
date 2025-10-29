package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository
import com.example.appstockcontrol_grupo_07.model.Categoria
import com.example.appstockcontrol_grupo_07.validation.Validators
import com.example.appstockcontrol_grupo_07.validation.ValidationResult

class FormularioCategoriaViewModel(
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioCategoriaState())
    val uiState: StateFlow<FormularioCategoriaState> = _uiState

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errores = it.errores.copy(nombre = null)) }
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion, errores = it.errores.copy(descripcion = null)) }
    }

    fun onActivaChange(activa: Boolean) {
        _uiState.update { it.copy(activa = activa) }
    }

    fun cargarCategoria(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val categoria = categoriaRepository.obtenerCategoriaPorId(id)
                categoria?.let {
                    _uiState.update { state ->
                        state.copy(
                            nombre = it.nombre,
                            descripcion = it.descripcion,
                            activa = it.activa,
                            fechaCreacion = it.fechaCreacion,
                            cargando = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar categoría: ${e.message}", cargando = false) }
            }
        }
    }

    fun guardarCategoria(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) return@launch

            _uiState.update { it.copy(cargando = true) }
            try {
                val categoria = Categoria(
                    nombre = _uiState.value.nombre,
                    descripcion = _uiState.value.descripcion,
                    fechaCreacion = System.currentTimeMillis(),
                    activa = _uiState.value.activa
                )
                categoriaRepository.agregarCategoria(categoria)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}", cargando = false) }
            }
        }
    }

    fun actualizarCategoria(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) return@launch

            _uiState.update { it.copy(cargando = true) }
            try {
                val categoria = Categoria(
                    id = id,
                    nombre = _uiState.value.nombre,
                    descripcion = _uiState.value.descripcion,
                    fechaCreacion = _uiState.value.fechaCreacion,
                    activa = _uiState.value.activa
                )
                categoriaRepository.actualizarCategoria(categoria)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar: ${e.message}", cargando = false) }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val errores = FormularioCategoriaErrores(
            nombre = when (val result = Validators.validateNonEmpty("Nombre", _uiState.value.nombre)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            descripcion = when (val result = Validators.validateNonEmpty("Descripción", _uiState.value.descripcion)) {
                is ValidationResult.Error -> result.message
                else -> null
            }
        )

        _uiState.update { it.copy(errores = errores) }

        return errores.nombre == null && errores.descripcion == null
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class FormularioCategoriaState(
    val nombre: String = "",
    val descripcion: String = "",
    val activa: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val cargando: Boolean = false,
    val error: String? = null,
    val errores: FormularioCategoriaErrores = FormularioCategoriaErrores()
)

data class FormularioCategoriaErrores(
    val nombre: String? = null,
    val descripcion: String? = null
)