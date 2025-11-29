package com.example.appstockcontrol_grupo_07.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class ProductoHalDto(
    val id: Long?,
    val nombre: String?,
    val descripcion: String?,
    val precio: Double?,
    val stock: Int?,
    val categoriaId: Long?,
    val activo: Boolean?
)

data class ProductosResponseDto(
    @SerializedName("_embedded")
    val embedded: Map<String, List<ProductoHalDto>>?
)

data class CategoriaRemotaDto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val activa: Boolean?
)

data class CategoriaCrearActualizarRequestDto(
    val nombre: String,
    val descripcion: String?,
    val activa: Boolean = true
)

data class CategoriaRefDto(
    val id: Long
)

data class ProductoCrearActualizarRequestDto(
    val nombre: String,
    val descripcion: String?,
    @SerializedName("stockActual") val stockActual: Int,
    @SerializedName("stockMinimo") val stockMinimo: Int,
    @SerializedName("precioUnitario") val precioUnitario: Double,
    val categoria: CategoriaRefDto,
    val activo: Boolean = true
)

interface CatalogoApi {

    @GET("api/catalogo/productos")
    suspend fun listarProductos(): ProductosResponseDto

    @GET("api/catalogo/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoHalDto

    @POST("api/catalogo/productos")
    suspend fun crearProducto(
        @Body request: ProductoCrearActualizarRequestDto
    ): ProductoHalDto

    @PUT("api/catalogo/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body request: ProductoCrearActualizarRequestDto
    ): ProductoHalDto

    @DELETE("api/catalogo/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Unit>

    @GET("api/catalogo/categorias")
    suspend fun listarCategorias(): List<CategoriaRemotaDto>

    @GET("api/catalogo/categorias/{id}")
    suspend fun obtenerCategoria(
        @Path("id") id: Long
    ): CategoriaRemotaDto

    @POST("api/catalogo/categorias")
    suspend fun crearCategoria(
        @Body request: CategoriaCrearActualizarRequestDto
    ): CategoriaRemotaDto

    @PUT("api/catalogo/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body request: CategoriaCrearActualizarRequestDto
    ): CategoriaRemotaDto

    @DELETE("api/catalogo/categorias/{id}")
    suspend fun eliminarCategoria(
        @Path("id") id: Long
    ): Response<Unit>
}
