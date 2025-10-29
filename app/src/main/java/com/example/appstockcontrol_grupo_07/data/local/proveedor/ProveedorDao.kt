package com.example.appstockcontrol_grupo_07.data.local.proveedor

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProveedorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(proveedor: ProveedorEntity): Long

    @Query("SELECT * FROM proveedores ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ProveedorEntity>>

    @Query("SELECT * FROM proveedores WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ProveedorEntity?

    @Delete
    suspend fun eliminar(proveedor: ProveedorEntity)

    @Query("DELETE FROM proveedores WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Update
    suspend fun actualizar(proveedor: ProveedorEntity)

    @Query("SELECT * FROM proveedores WHERE nombre LIKE '%' || :query || '%' OR contacto LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun buscar(query: String): Flow<List<ProveedorEntity>>

    @Query("SELECT COUNT(*) FROM proveedores")
    suspend fun count(): Int
}