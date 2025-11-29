package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaDao
import com.example.appstockcontrol_grupo_07.model.Categoria
import com.example.appstockcontrol_grupo_07.remote.CatalogoApi
import com.example.appstockcontrol_grupo_07.remote.CategoriaCrearActualizarRequestDto
import com.example.appstockcontrol_grupo_07.remote.CategoriaRemotaDto
import com.example.appstockcontrol_grupo_07.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CategoriaRepository(
    private val categoriaDao: CategoriaDao? = null,
    private val api: CatalogoApi = RetrofitInstance.catalogoApi
) {

    fun obtenerCategorias(): Flow<List<Categoria>> = flow {
        val remotas = api.listarCategorias()
        val lista = remotas.map { it.toCategoria() }
        emit(lista)
    }

    suspend fun agregarCategoria(categoria: Categoria) {
        val request = CategoriaCrearActualizarRequestDto(
            nombre = categoria.nombre,
            descripcion = categoria.descripcion,
            activa = categoria.activa
        )
        api.crearCategoria(request)
    }

    suspend fun actualizarCategoria(categoria: Categoria) {
        val request = CategoriaCrearActualizarRequestDto(
            nombre = categoria.nombre,
            descripcion = categoria.descripcion,
            activa = categoria.activa
        )
        api.actualizarCategoria(categoria.id.toLong(), request)
    }

    suspend fun eliminarCategoria(id: Int) {
        val response = api.eliminarCategoria(id.toLong())
        if (!response.isSuccessful) {
            throw IllegalStateException("Error al eliminar categoría: ${response.code()}")
        }
    }

    suspend fun obtenerCategoriaPorId(id: Int): Categoria? {
        val remota = api.obtenerCategoria(id.toLong())
        return remota.toCategoria()
    }

    fun buscarCategorias(query: String): Flow<List<Categoria>> =
        obtenerCategorias().map { lista ->
            lista.filter { cat ->
                cat.nombre.contains(query, ignoreCase = true) ||
                        cat.descripcion.contains(query, ignoreCase = true)
            }
        }
}

private fun CategoriaRemotaDto.toCategoria(): Categoria =
    Categoria(
        id = this.id.toInt(),
        nombre = this.nombre,
        descripcion = this.descripcion ?: "",
        fechaCreacion = 0L,
        activa = this.activa ?: true
    )
