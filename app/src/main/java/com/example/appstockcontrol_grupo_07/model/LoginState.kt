package com.example.appstockcontrol_grupo_07.model

data class LoginState(
    val correo: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores(),
    val errorAutenticacion: String? = null,  // ← AGREGAR ESTE CAMPO
    val cargando: Boolean = false,
    val autenticacionExitosa: Boolean = false// ← AGREGAR ESTE CAMPO
)