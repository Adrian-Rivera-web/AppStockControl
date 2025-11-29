package com.example.appstockcontrol_grupo_07.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): UsuarioResponseDto

    @POST("api/auth/registro")
    suspend fun registrar(
        @Body request: RegistroRequestDto
    ): UsuarioResponseDto

    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioResponseDto>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuarioPorId(
        @Path("id") id: Long
    ): UsuarioResponseDto

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Path("id") id: Long
    ): Response<Unit>
}
