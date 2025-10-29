package com.example.appstockcontrol_grupo_07.data.local.producto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(producto: ProductoEntity): Long

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getById(id: Int): ProductoEntity?

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun delete(id: Int)

    @Update
    suspend fun update(producto: ProductoEntity)

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%' OR categoria LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ProductoEntity>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun count(): Int
}