package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.producto.ProductoDao
import com.example.appstockcontrol_grupo_07.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductoRepository(
    private val productoDao: ProductoDao // ✅ Recibe ProductoDao
) {

    fun obtenerProductos(): Flow<List<Producto>> =
        productoDao.getAll().map { entities ->
            entities.map { it.toProducto() }
        }

    suspend fun agregarProducto(producto: Producto) {
        productoDao.insert(producto.toEntity())
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.update(producto.toEntity())
    }

    suspend fun eliminarProducto(id: Int) {
        productoDao.delete(id)
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.getById(id)?.toProducto()
    }

    fun buscarProductos(query: String): Flow<List<Producto>> =
        productoDao.getAll().map { entities ->
            entities.filter { producto ->
                producto.nombre.contains(query, ignoreCase = true) ||
                        producto.descripcion.contains(query, ignoreCase = true) ||
                        producto.categoria.contains(query, ignoreCase = true)
            }.map { it.toProducto() }
        }
}

// Extension functions para convertir entre Entity y Model
private fun com.example.appstockcontrol_grupo_07.data.local.producto.ProductoEntity.toProducto(): Producto =
    Producto(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        stock = this.stock,
        categoria = this.categoria,
        proveedor = this.proveedor
    )

private fun Producto.toEntity(): com.example.appstockcontrol_grupo_07.data.local.producto.ProductoEntity =
    com.example.appstockcontrol_grupo_07.data.local.producto.ProductoEntity(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        stock = this.stock,
        categoria = this.categoria,
        proveedor = this.proveedor
    )