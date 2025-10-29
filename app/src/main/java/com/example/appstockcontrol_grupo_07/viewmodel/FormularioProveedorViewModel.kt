package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.ProveedorRepository
import com.example.appstockcontrol_grupo_07.model.Proveedor
import com.example.appstockcontrol_grupo_07.validation.Validators
import com.example.appstockcontrol_grupo_07.validation.ValidationResult

class FormularioProveedorViewModel(
    private val proveedorRepository: ProveedorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioProveedorState())
    val uiState: StateFlow<FormularioProveedorState> = _uiState

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errores = it.errores.copy(nombre = null)) }
    }

    fun onContactoChange(contacto: String) {
        _uiState.update { it.copy(contacto = contacto, errores = it.errores.copy(contacto = null)) }
    }

    fun onTelefonoChange(telefono: String) {
        _uiState.update { it.copy(telefono = telefono, errores = it.errores.copy(telefono = null)) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errores = it.errores.copy(email = null)) }
    }

    fun onDireccionChange(direccion: String) {
        _uiState.update { it.copy(direccion = direccion, errores = it.errores.copy(direccion = null)) }
    }

    fun onActivoChange(activo: Boolean) {
        _uiState.update { it.copy(activo = activo) }
    }

    fun cargarProveedor(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val proveedor = proveedorRepository.obtenerProveedorPorId(id)
                proveedor?.let {
                    _uiState.update { state ->
                        state.copy(
                            nombre = it.nombre,
                            contacto = it.contacto,
                            telefono = it.telefono,
                            email = it.email,
                            direccion = it.direccion,
                            activo = it.activo,
                            cargando = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar proveedor: ${e.message}", cargando = false) }
            }
        }
    }

    fun guardarProveedor(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) return@launch

            _uiState.update { it.copy(cargando = true) }
            try {
                val proveedor = Proveedor(
                    nombre = _uiState.value.nombre,
                    contacto = _uiState.value.contacto,
                    telefono = _uiState.value.telefono,
                    email = _uiState.value.email,
                    direccion = _uiState.value.direccion,
                    activo = _uiState.value.activo
                )
                proveedorRepository.agregarProveedor(proveedor)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}", cargando = false) }
            }
        }
    }

    fun actualizarProveedor(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) return@launch

            _uiState.update { it.copy(cargando = true) }
            try {
                val proveedor = Proveedor(
                    id = id,
                    nombre = _uiState.value.nombre,
                    contacto = _uiState.value.contacto,
                    telefono = _uiState.value.telefono,
                    email = _uiState.value.email,
                    direccion = _uiState.value.direccion,
                    activo = _uiState.value.activo
                )
                proveedorRepository.actualizarProveedor(proveedor)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar: ${e.message}", cargando = false) }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val errores = FormularioProveedorErrores(
            nombre = when (val result = Validators.validateNonEmpty("Nombre", _uiState.value.nombre)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            contacto = when (val result = Validators.validateNonEmpty("Contacto", _uiState.value.contacto)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            telefono = when (val result = Validators.validatePhone(_uiState.value.telefono)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            email = when (val result = Validators.validateEmail(_uiState.value.email)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            direccion = when (val result = Validators.validateNonEmpty("Dirección", _uiState.value.direccion)) {
                is ValidationResult.Error -> result.message
                else -> null
            }
        )

        _uiState.update { it.copy(errores = errores) }

        return errores.nombre == null &&
                errores.contacto == null &&
                errores.telefono == null &&
                errores.email == null &&
                errores.direccion == null
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class FormularioProveedorState(
    val nombre: String = "",
    val contacto: String = "",
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
    val activo: Boolean = true,
    val cargando: Boolean = false,
    val error: String? = null,
    val errores: FormularioProveedorErrores = FormularioProveedorErrores()
)

data class FormularioProveedorErrores(
    val nombre: String? = null,
    val contacto: String? = null,
    val telefono: String? = null,
    val email: String? = null,
    val direccion: String? = null
)