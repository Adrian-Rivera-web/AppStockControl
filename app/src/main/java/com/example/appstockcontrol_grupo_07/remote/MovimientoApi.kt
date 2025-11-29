package com.example.appstockcontrol_grupo_07.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Coincide con MovimientoDTO del backend
data class MovimientoRemotoDto(
    val id: Long? = null,
    val productoId: Long? = null,
    val tipo: String? = null,              // "ENTRADA" | "SALIDA"
    val cantidad: Int? = null,
    val stockAnterior: Int? = null,
    val stockNuevo: Int? = null,
    val usuarioResponsable: String? = null,
    val motivo: String? = null,
    val fechaHora: String? = null          // LocalDateTime en backend (string ISO)
)

// Coinciden con RegistrarEntradaRequest / RegistrarSalidaRequest del backend
data class RegistrarEntradaRemoteRequest(
    val productoId: Long,
    val cantidad: Int,
    val usuarioResponsable: String,
    val motivo: String
)

data class RegistrarSalidaRemoteRequest(
    val productoId: Long,
    val cantidad: Int,
    val usuarioResponsable: String,
    val motivo: String
)

interface MovimientoApi {

    // GET /api/movimientos
    @GET("api/movimientos")
    suspend fun listarMovimientos(): List<MovimientoRemotoDto>

    // GET /api/movimientos/producto/{productoId}
    @GET("api/movimientos/producto/{productoId}")
    suspend fun listarMovimientosPorProducto(
        @Path("productoId") productoId: Long
    ): List<MovimientoRemotoDto>

    // POST /api/movimientos/entrada?stockAnterior=..&stockNuevo=..
    @POST("api/movimientos/entrada")
    suspend fun registrarEntrada(
        @Body request: RegistrarEntradaRemoteRequest,
        @Query("stockAnterior") stockAnterior: Int,
        @Query("stockNuevo") stockNuevo: Int
    ): MovimientoRemotoDto

    // POST /api/movimientos/salida?stockAnterior=..&stockNuevo=..
    @POST("api/movimientos/salida")
    suspend fun registrarSalida(
        @Body request: RegistrarSalidaRemoteRequest,
        @Query("stockAnterior") stockAnterior: Int,
        @Query("stockNuevo") stockNuevo: Int
    ): MovimientoRemotoDto
}
