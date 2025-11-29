package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorDao
import com.example.appstockcontrol_grupo_07.model.Proveedor
import com.example.appstockcontrol_grupo_07.remote.ProveedorApi
import com.example.appstockcontrol_grupo_07.remote.ProveedorRemotoDto
import com.example.appstockcontrol_grupo_07.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProveedorRepository(
    // Lo dejamos opcional por compatibilidad, aunque ya no lo usamos para el CRUD principal
    private val proveedorDao: ProveedorDao? = null,
    private val api: ProveedorApi = RetrofitInstance.proveedorApi
) {

    // ========= READ: listar todos los proveedores (activos) =========
    fun obtenerProveedores(): Flow<List<Proveedor>> = flow {
        val remotos = api.listarProveedores()
        emit(remotos.map { it.toProveedor() })
    }

    // ========= SEARCH: buscar por nombre/contacto/email =========
    fun buscarProveedores(query: String): Flow<List<Proveedor>> = flow {
        val remotos = if (query.isBlank()) {
            api.listarProveedores()
        } else {
            api.buscarProveedores(query)
        }
        emit(remotos.map { it.toProveedor() })
    }

    // ========= CREATE: agregar proveedor =========
    suspend fun agregarProveedor(proveedor: Proveedor) {
        val dto = proveedor.toDto()
        api.crearProveedor(dto)
    }

    // ========= UPDATE: actualizar proveedor =========
    suspend fun actualizarProveedor(proveedor: Proveedor) {
        val dto = proveedor.toDto()
        api.actualizarProveedor(proveedor.id.toLong(), dto)
    }

    // ========= DELETE: eliminar proveedor (borrado lógico) =========
    suspend fun eliminarProveedor(id: Int) {
        val resp = api.eliminarProveedor(id.toLong())
        if (!resp.isSuccessful) {
            throw IllegalStateException("Error al eliminar proveedor: ${resp.code()}")
        }
    }

    // ========= READ: obtener proveedor por id =========
    suspend fun obtenerProveedorPorId(id: Int): Proveedor? {
        val dto = api.obtenerProveedor(id.toLong())
        return dto.toProveedor()
    }
}

// =========================
// Mapeos DTO ↔ modelo app
// =========================

private fun ProveedorRemotoDto.toProveedor(): Proveedor =
    Proveedor(
        id = this.id?.toInt() ?: 0,
        nombre = this.nombre,
        contacto = this.contacto,
        telefono = this.telefono,
        email = this.email,
        direccion = this.direccion,
        activo = this.activo
    )

private fun Proveedor.toDto(): ProveedorRemotoDto =
    ProveedorRemotoDto(
        id = if (this.id == 0) null else this.id.toLong(),
        nombre = this.nombre,
        contacto = this.contacto,
        telefono = this.telefono,
        email = this.email,
        direccion = this.direccion,
        activo = this.activo
    )
