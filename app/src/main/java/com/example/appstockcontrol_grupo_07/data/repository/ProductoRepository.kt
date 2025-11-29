package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.producto.ProductoDao
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.remote.CatalogoApi
import com.example.appstockcontrol_grupo_07.remote.CategoriaRefDto
import com.example.appstockcontrol_grupo_07.remote.ProductoCrearActualizarRequestDto
import com.example.appstockcontrol_grupo_07.remote.ProductoHalDto
import com.example.appstockcontrol_grupo_07.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProductoRepository(
    // Lo dejamos opcional para no romper otras partes (por si quieres usar Room después)
    private val productoDao: ProductoDao? = null,
    private val api: CatalogoApi = RetrofitInstance.catalogoApi
) {

    // =========================
    // LISTAR PRODUCTOS (desde backend)
    // =========================
    fun obtenerProductos(): Flow<List<Producto>> = flow {
        // 1) Traemos categorías
        val categorias = api.listarCategorias()
        val mapaCategorias = categorias.associateBy { it.id }

        // 2) Traemos la lista HATEOAS de productos
        val respuesta = api.listarProductos()
        val listaRemota: List<ProductoHalDto> =
            respuesta.embedded?.values?.firstOrNull() ?: emptyList()

        // 3) Mapeamos a tu modelo Producto
        val listaApp = listaRemota.map { remoto ->
            val nombreCategoria = remoto.categoriaId?.let { idCat ->
                mapaCategorias[idCat]?.nombre ?: "Sin categoría"
            } ?: "Sin categoría"

            remoto.toProducto(nombreCategoria)
        }

        emit(listaApp)
    }
    // =========================
    // CREAR PRODUCTO
    // =========================
    suspend fun agregarProducto(producto: Producto) {
        // Buscamos la categoría por nombre en el backend
        val categorias = api.listarCategorias()
        val categoria = categorias.firstOrNull {
            it.nombre.equals(producto.categoria, ignoreCase = true)
        } ?: throw IllegalStateException("La categoría '${producto.categoria}' no existe en el servidor")

        val request = ProductoCrearActualizarRequestDto(
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            stockActual = producto.stock,
            stockMinimo = producto.stockMinimo,
            precioUnitario = producto.precio,
            categoria = CategoriaRefDto(id = categoria.id),
            activo = true
        )

        api.crearProducto(request)
    }

    // =========================
    // ACTUALIZAR PRODUCTO
    // =========================
    suspend fun actualizarProducto(producto: Producto) {
        val categorias = api.listarCategorias()
        val categoria = categorias.firstOrNull {
            it.nombre.equals(producto.categoria, ignoreCase = true)
        } ?: throw IllegalStateException("La categoría '${producto.categoria}' no existe en el servidor")

        val request = ProductoCrearActualizarRequestDto(
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            stockActual = producto.stock,
            stockMinimo = producto.stockMinimo,
            precioUnitario = producto.precio,
            categoria = CategoriaRefDto(id = categoria.id),
            activo = true
        )

        api.actualizarProducto(producto.id.toLong(), request)
    }

    // =========================
    // ELIMINAR PRODUCTO
    // =========================
    suspend fun eliminarProducto(id: Int) {
        val response = api.eliminarProducto(id.toLong())
        if (!response.isSuccessful) {
            throw IllegalStateException("Error al eliminar producto: ${response.code()}")
        }
    }

    // =========================
    // OBTENER PRODUCTO POR ID
    // =========================
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        val categorias = api.listarCategorias()
        val mapaCategorias = categorias.associateBy { it.id }

        val remoto = api.obtenerProducto(id.toLong())
        val nombreCategoria = remoto.categoriaId?.let { idCat ->
            mapaCategorias[idCat]?.nombre ?: "Sin categoría"
        } ?: "Sin categoría"

        return remoto.toProducto(nombreCategoria)
    }

    // =========================
    // BUSCAR PRODUCTOS (filtrado en memoria)
    // =========================
    fun buscarProductos(query: String): Flow<List<Producto>> =
        obtenerProductos().map { lista ->
            lista.filter { producto ->
                producto.nombre.contains(query, ignoreCase = true) ||
                        producto.descripcion.contains(query, ignoreCase = true) ||
                        producto.categoria.contains(query, ignoreCase = true)
            }
        }
}

// =========================
// Mapeo de DTO remoto → modelo de la app
// =========================

private fun ProductoHalDto.toProducto(nombreCategoria: String): Producto =
    Producto(
        id = this.id?.toInt() ?: 0,
        nombre = this.nombre ?: "",
        descripcion = this.descripcion ?: "",
        precio = this.precio ?: 0.0,
        stock = this.stock ?: 0,
        stockMinimo = 0,            // el backend no expone stockMinimo en el DTO, lo dejamos en 0
        categoria = nombreCategoria,
        proveedor = "",             // el backend no maneja proveedor
        fechaCreacion = "",
        fechaActualizacion = "",
        imagenUri = null
    )
