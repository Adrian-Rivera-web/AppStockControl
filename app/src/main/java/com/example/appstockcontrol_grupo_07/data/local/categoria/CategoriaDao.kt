package com.example.appstockcontrol_grupo_07.data.local.categoria

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: CategoriaEntity): Long

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun obtenerPorId(id: Int): CategoriaEntity?

    @Delete
    suspend fun eliminar(categoria: CategoriaEntity)

    @Query("DELETE FROM categorias WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Update
    suspend fun actualizar(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias WHERE nombre LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%'")
    fun buscar(query: String): Flow<List<CategoriaEntity>>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun count(): Int
}