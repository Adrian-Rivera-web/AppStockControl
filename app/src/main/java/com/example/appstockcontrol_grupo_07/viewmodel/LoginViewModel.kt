package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.model.LoginState
import com.example.appstockcontrol_grupo_07.model.LoginErrores
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.validation.Validators
import com.example.appstockcontrol_grupo_07.validation.ValidationResult
import com.example.appstockcontrol_grupo_07.validation.PasswordLevel

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _estado = MutableStateFlow(LoginState())
    val estado: StateFlow<LoginState> = _estado

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun autenticarUsuario(
        onResult: (Boolean, String?, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            println("DEBUG: LoginViewModel - Iniciando autenticación...")

            if (!validarLogin()) {
                println("DEBUG: LoginViewModel - ❌ Validación de formulario falló")
                onResult(false, "Por favor corrige los errores del formulario", false)
                return@launch
            }

            _estado.update { it.copy(cargando = true, errorAutenticacion = null) }

            try {
                delay(800) // Simulamos delay de red

                val estadoActual = _estado.value
                println("DEBUG: LoginViewModel - Autenticando: ${estadoActual.correo}")

                val result = userRepository.login(estadoActual.correo, estadoActual.clave)

                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    println("DEBUG: LoginViewModel - ✅ AUTENTICACIÓN EXITOSA")
                    println("DEBUG: LoginViewModel - Usuario: ${user.email}, isAdmin: ${user.isAdmin}")

                    _estado.update {
                        it.copy(
                            cargando = false,
                            autenticacionExitosa = true,
                            errorAutenticacion = null
                        )
                    }
                    // ✅ PASAR isAdmin CORRECTAMENTE
                    onResult(true, user.name, user.isAdmin)
                } else {
                    println("DEBUG: LoginViewModel - ❌ Error en autenticación: ${result.exceptionOrNull()?.message}")
                    _estado.update {
                        it.copy(
                            cargando = false,
                            errorAutenticacion = "Usuario no registrado o credenciales incorrectas"
                        )
                    }
                    onResult(false, "Acceso denegado", false)
                }
            } catch (e: Exception) {
                println("DEBUG: LoginViewModel - ❌ Excepción: ${e.message}")
                _estado.update {
                    it.copy(
                        cargando = false,
                        errorAutenticacion = "Error del sistema"
                    )
                }
                onResult(false, "Error de conexión", false)
            }
        }
    }

    fun validarLogin(): Boolean {
        val estadoActual = _estado.value
        val errores = LoginErrores(
            correo = when (val result = Validators.validateEmail(estadoActual.correo)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            clave = when (val result = Validators.validatePassword(estadoActual.clave, PasswordLevel.BASIC)) {
                is ValidationResult.Error -> result.message
                else -> null
            }
        )

        val formularioValido = errores.correo == null && errores.clave == null
        _estado.update { it.copy(errores = errores) }
        return formularioValido
    }

    fun limpiarFormulario() {
        _estado.value = LoginState()
    }
}