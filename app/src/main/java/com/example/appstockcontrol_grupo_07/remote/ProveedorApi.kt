package com.example.appstockcontrol_grupo_07.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Coincide con ProveedorDTO.java del backend
data class ProveedorRemotoDto(
    val id: Long? = null,
    val nombre: String,
    val contacto: String,
    val telefono: String,
    val email: String,
    val direccion: String,
    val activo: Boolean
)

interface ProveedorApi {

    // GET /api/proveedores
    @GET("api/proveedores")
    suspend fun listarProveedores(): List<ProveedorRemotoDto>

    // GET /api/proveedores/search?query=...
    @GET("api/proveedores/search")
    suspend fun buscarProveedores(
        @Query("query") query: String
    ): List<ProveedorRemotoDto>

    // GET /api/proveedores/{id}
    @GET("api/proveedores/{id}")
    suspend fun obtenerProveedor(
        @Path("id") id: Long
    ): ProveedorRemotoDto

    // POST /api/proveedores
    @POST("api/proveedores")
    suspend fun crearProveedor(
        @Body dto: ProveedorRemotoDto
    ): ProveedorRemotoDto

    // PUT /api/proveedores/{id}
    @PUT("api/proveedores/{id}")
    suspend fun actualizarProveedor(
        @Path("id") id: Long,
        @Body dto: ProveedorRemotoDto
    ): ProveedorRemotoDto

    // DELETE /api/proveedores/{id}
    @DELETE("api/proveedores/{id}")
    suspend fun eliminarProveedor(
        @Path("id") id: Long
    ): Response<Unit>
}
