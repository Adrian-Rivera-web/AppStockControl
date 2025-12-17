package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.remote.CatalogoApi
import com.example.appstockcontrol_grupo_07.remote.CategoriaRemotaDto
import com.example.appstockcontrol_grupo_07.remote.ProductoHalDto
import com.example.appstockcontrol_grupo_07.remote.ProductosResponseDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class ProductoRepositoryTest : StringSpec({

    "obtenerProductos() debe mapear productos HAL + categorias a Producto (con nombreCategoria)" {

        // 1) Mock API
        val mockApi = mockk<CatalogoApi>()

        // 2) Datos falsos (categorías)
        val categoriasFalsas = listOf(
            CategoriaRemotaDto(id = 1, nombre = "Electrónicos", descripcion = null, activa = true)
        )
        coEvery { mockApi.listarCategorias() } returns categoriasFalsas

        // 3) Datos falsos (respuesta HATEOAS de productos)
        val productosHal = listOf(
            ProductoHalDto(
                id = 10L,
                nombre = "Mouse",
                descripcion = "Logitech",
                precio = 10000.0,
                stock = 10,
                categoriaId = 1L,
                activo = true
            )
        )
        coEvery { mockApi.listarProductos() } returns ProductosResponseDto(
            embedded = mapOf("productos" to productosHal)
        )

        // 4) Repo usando el API mockeado (no necesitas subclase)
        val repo = ProductoRepository(productoDao = null, api = mockApi)

        // 5) Ejecutar y validar
        runTest {
            val resultado = repo.obtenerProductos().first()

            val esperado = listOf(
                Producto(
                    id = 10,
                    nombre = "Mouse",
                    descripcion = "Logitech",
                    precio = 10000.0,
                    stock = 10,
                    stockMinimo = 0,
                    categoria = "Electrónicos",
                    proveedor = "",
                    fechaCreacion = "",
                    fechaActualizacion = "",
                    imagenUri = null
                )
            )

            resultado shouldContainExactly esperado
        }
    }
})
