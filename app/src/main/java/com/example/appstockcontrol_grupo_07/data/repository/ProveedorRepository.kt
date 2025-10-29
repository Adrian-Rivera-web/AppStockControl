package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorDao
import com.example.appstockcontrol_grupo_07.model.Proveedor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProveedorRepository(
    private val proveedorDao: ProveedorDao
) {

    fun obtenerProveedores(): Flow<List<Proveedor>> =
        proveedorDao.obtenerTodos().map { entities ->
            entities.map { it.toProveedor() }
        }

    suspend fun agregarProveedor(proveedor: Proveedor) {
        proveedorDao.insertar(proveedor.toEntity())
    }

    suspend fun actualizarProveedor(proveedor: Proveedor) {
        proveedorDao.actualizar(proveedor.toEntity())
    }

    suspend fun eliminarProveedor(id: Int) {
        proveedorDao.eliminar(id)
    }

    suspend fun obtenerProveedorPorId(id: Int): Proveedor? {
        return proveedorDao.obtenerPorId(id)?.toProveedor()
    }

    fun buscarProveedores(query: String): Flow<List<Proveedor>> =
        proveedorDao.buscar(query).map { entities ->
            entities.map { it.toProveedor() }
        }
}

// Extension functions para convertir entre Entity y Model
private fun com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorEntity.toProveedor(): Proveedor =
    Proveedor(
        id = this.id,
        nombre = this.nombre,
        contacto = this.contacto,
        telefono = this.telefono,
        email = this.email,
        direccion = this.direccion,
        activo = this.activo == 1
    )

private fun Proveedor.toEntity(): com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorEntity =
    com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorEntity(
        id = this.id,
        nombre = this.nombre,
        contacto = this.contacto,
        telefono = this.telefono,
        email = this.email,
        direccion = this.direccion,
        activo = if (this.activo) 1 else 0
    )