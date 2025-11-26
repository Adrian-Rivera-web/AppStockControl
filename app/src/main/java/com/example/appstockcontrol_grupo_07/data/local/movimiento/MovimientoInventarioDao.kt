package com.example.appstockcontrol_grupo_07.data.local.movimiento

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoInventarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(movimiento: MovimientoInventarioEntity): Long

    @Query("SELECT * FROM movimientos_inventario ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<MovimientoInventarioEntity>>

    @Query("SELECT * FROM movimientos_inventario WHERE productoId = :productoId ORDER BY fecha DESC")
    fun obtenerPorProducto(productoId: Int): Flow<List<MovimientoInventarioEntity>>

    @Query("DELETE FROM movimientos_inventario")
    suspend fun eliminarTodos()
}
