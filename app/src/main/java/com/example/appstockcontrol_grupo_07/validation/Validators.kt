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
    fun validateNonEmpty(fieldName: String, value: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Error("$fieldName es requerido")
        } else {
            ValidationResult.Success
        }
    }

    // Validar precio
    fun validatePrice(price: String): ValidationResult {
        return try {
            if (price.isBlank()) {
                ValidationResult.Error("Precio es requerido")
            } else {
                val priceValue = price.toDouble()
                if (priceValue <= 0) {
                    ValidationResult.Error("Precio debe ser mayor a 0")
                } else {
                    ValidationResult.Success
                }
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Precio debe ser un número válido")
        }
    }

    // Validar stock
    fun validateStock(stock: String): ValidationResult {
        return try {
            if (stock.isBlank()) {
                ValidationResult.Error("Stock es requerido")
            } else {
                val stockValue = stock.toInt()
                if (stockValue < 0) {
                    ValidationResult.Error("Stock no puede ser negativo")
                } else {
                    ValidationResult.Success
                }
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Error("Stock debe ser un número válido")
        }
    }

    // Validar longitud mínima
    fun validateMinLength(fieldName: String, value: String, minLength: Int): ValidationResult {
        return if (value.length < minLength) {
            ValidationResult.Error("$fieldName debe tener al menos $minLength caracteres")
        } else {
            ValidationResult.Success
        }
    }

    // Validar longitud máxima
    fun validateMaxLength(fieldName: String, value: String, maxLength: Int): ValidationResult {
        return if (value.length > maxLength) {
            ValidationResult.Error("$fieldName no puede exceder $maxLength caracteres")
        } else {
            ValidationResult.Success
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