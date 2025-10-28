package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsuarioViewModel : ViewModel() {
    private val _usuarioLogueado = MutableStateFlow<String?>(null)
    val usuarioLogueado: StateFlow<String?> = _usuarioLogueado

    private val _nombreUsuario = MutableStateFlow<String?>(null)
    val nombreUsuario: StateFlow<String?> = _nombreUsuario

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin

    fun iniciarSesion(correo: String, esAdmin: Boolean = false, nombre: String? = null) {
        println("DEBUG: UsuarioViewModel - Iniciando sesión - correo: $correo, esAdmin: $esAdmin, nombre: $nombre")
        _usuarioLogueado.value = correo
        _esAdmin.value = esAdmin
        _nombreUsuario.value = nombre
    }

    fun cerrarSesion() {
        println("DEBUG: UsuarioViewModel - === INICIANDO CIERRE DE SESIÓN ===")
        println("DEBUG: UsuarioViewModel - Estado anterior: usuario=${_usuarioLogueado.value}, esAdmin=${_esAdmin.value}")

        _usuarioLogueado.value = null
        _esAdmin.value = false
        _nombreUsuario.value = null

        println("DEBUG: UsuarioViewModel - Estado actual: usuario=${_usuarioLogueado.value}, esAdmin=${_esAdmin.value}")
        println("DEBUG: UsuarioViewModel - === CIERRE DE SESIÓN COMPLETADO ===")
    }
}