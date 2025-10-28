package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.model.RegistroState
import com.example.appstockcontrol_grupo_07.model.RegistroErrores
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.validation.Validators
import com.example.appstockcontrol_grupo_07.validation.ValidationResult
import com.example.appstockcontrol_grupo_07.validation.PasswordLevel

class RegistroViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroState())
    val estado: StateFlow<RegistroState> = _estado

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _registrationResult = MutableStateFlow<Result<Long>?>(null)
    val registrationResult: StateFlow<Result<Long>?> = _registrationResult

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    // NUEVA FUNCIÓN para teléfono
    fun onTelefonoChange(valor: String) {
        _estado.update {
            it.copy(
                telefono = valor,
                errores = it.errores.copy(telefono = null)
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                errores = it.errores.copy(
                    clave = null,
                    claveConfirmacion = null
                )
            )
        }
    }

    fun onClaveConfirmacionChange(valor: String) {
        _estado.update {
            it.copy(
                claveConfirmacion = valor,
                errores = it.errores.copy(claveConfirmacion = null)
            )
        }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = RegistroErrores(
            nombre = when (val result = Validators.validateName(estadoActual.nombre)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            correo = when (val result = Validators.validateEmail(estadoActual.correo)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            // AQUÍ USAMOS LA FUNCIÓN validatePhone
            telefono = when (val result = Validators.validatePhone(estadoActual.telefono)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            clave = when (val result = Validators.validatePassword(estadoActual.clave, PasswordLevel.STRONG)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            claveConfirmacion = when (val result = Validators.validatePasswordConfirm(
                estadoActual.clave,
                estadoActual.claveConfirmacion
            )) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            direccion = if (estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )

        val formularioValido = errores.nombre == null &&
                errores.correo == null &&
                errores.telefono == null && // ← INCLUIR EN LA VALIDACIÓN
                errores.clave == null &&
                errores.claveConfirmacion == null &&
                errores.direccion == null &&
                estadoActual.aceptaTerminos

        _estado.update { it.copy(errores = errores) }
        return formularioValido
    }

    fun registrarUsuario(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) {
                onResult(false, "Por favor corrige los errores del formulario")
                return@launch
            }

            _isLoading.update { true }
            _registrationResult.update { null }

            try {
                val estadoActual = _estado.value

                val result = userRepository.register(
                    name = estadoActual.nombre,
                    email = estadoActual.correo,
                    phone = estadoActual.telefono, // ← AHORA USAMOS EL TELÉFONO DEL FORMULARIO
                    password = estadoActual.clave,
                    address = estadoActual.direccion,
                    isAdmin = false
                )

                _registrationResult.update { result }

                if (result.isSuccess) {
                    onResult(true, "Registro exitoso")
                    limpiarFormulario()
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    onResult(false, errorMessage)
                }

            } catch (e: Exception) {
                onResult(false, "Error de conexión: ${e.message}")
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun limpiarFormulario() {
        _estado.value = RegistroState()
        _registrationResult.update { null }
    }

    fun clearRegistrationResult() {
        _registrationResult.update { null }
    }
}