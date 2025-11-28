package com.example.appstockcontrol_grupo_07.remote

data class LoginRequestDto(
    val correo: String,
    val clave: String
)

data class RegistroRequestDto(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val clave: String,
    val direccion: String,
    val aceptaTerminos: Boolean
)

data class UsuarioResponseDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val esAdmin: Boolean
)
