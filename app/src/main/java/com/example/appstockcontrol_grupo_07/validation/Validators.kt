package com.example.appstockcontrol_grupo_07.validation

import android.util.Patterns

object Validators {

    // Valida email con más opciones
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("El email es obligatorio")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Error("Formato de email inválido")
            else -> ValidationResult.Success
        }
    }

    // Valida nombre con más flexibilidad
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("El nombre es obligatorio")
            name.length < 2 -> ValidationResult.Error("Mínimo 2 caracteres")
            !Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ\\s]+\$").matches(name) ->
                ValidationResult.Error("Solo letras y espacios")
            else -> ValidationResult.Success
        }
    }

    // Valida teléfono
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult.Error("El teléfono es obligatorio")
            !phone.all { it.isDigit() } -> ValidationResult.Error("Solo números")
            phone.length !in 8..15 -> ValidationResult.Error("8-15 dígitos")
            else -> ValidationResult.Success
        }
    }

    // Valida contraseña con diferentes niveles
    fun validatePassword(password: String, level: PasswordLevel = PasswordLevel.STRONG): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("La contraseña es obligatoria")
            password.length < 6 -> ValidationResult.Error("Mínimo 6 caracteres")
            level == PasswordLevel.STRONG -> validateStrongPassword(password)
            else -> ValidationResult.Success
        }
    }

    private fun validateStrongPassword(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (password.length < 8) errors.add("Mínimo 8 caracteres")
        if (!password.any { it.isUpperCase() }) errors.add("Una mayúscula")
        if (!password.any { it.isLowerCase() }) errors.add("Una minúscula")
        if (!password.any { it.isDigit() }) errors.add("Un número")
        if (!password.any { !it.isLetterOrDigit() }) errors.add("Un símbolo")
        if (password.contains(' ')) errors.add("Sin espacios")

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Requisitos: ${errors.joinToString(", ")}")
        }
    }

    // Valida confirmación de contraseña
    fun validatePasswordConfirm(password: String, confirm: String): ValidationResult {
        return when {
            confirm.isBlank() -> ValidationResult.Error("Confirma tu contraseña")
            password != confirm -> ValidationResult.Error("Las contraseñas no coinciden")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

enum class PasswordLevel {
    BASIC, // Solo longitud mínima
    STRONG // Con todos los requisitos
}