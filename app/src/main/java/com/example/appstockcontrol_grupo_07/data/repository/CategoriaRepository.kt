package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaDao
import com.example.appstockcontrol_grupo_07.model.Categoria
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriaRepository(
    private val categoriaDao: CategoriaDao
) {

    fun obtenerCategorias(): Flow<List<Categoria>> =
        categoriaDao.obtenerTodas().map { entities ->
            entities.map { it.toCategoria() }
        }

    suspend fun agregarCategoria(categoria: Categoria) {
        categoriaDao.insertar(categoria.toEntity())
    }

    suspend fun actualizarCategoria(categoria: Categoria) {
        categoriaDao.actualizar(categoria.toEntity())
    }

    suspend fun eliminarCategoria(id: Int) {
        categoriaDao.eliminar(id)
    }

    suspend fun obtenerCategoriaPorId(id: Int): Categoria? {
        return categoriaDao.obtenerPorId(id)?.toCategoria()
    }

    fun buscarCategorias(query: String): Flow<List<Categoria>> =
        categoriaDao.buscar(query).map { entities ->
            entities.map { it.toCategoria() }
        }
}

// Extension functions para convertir entre Entity y Model
private fun com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaEntity.toCategoria(): Categoria =
    Categoria(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        fechaCreacion = this.fechaCreacion,
        activa = this.activa == 1
    )

private fun Categoria.toEntity(): com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaEntity =
    com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaEntity(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        fechaCreacion = this.fechaCreacion,
        activa = if (this.activa) 1 else 0
    )