package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.model.MovimientoInventario
import com.example.appstockcontrol_grupo_07.model.TipoMovimiento
import com.example.appstockcontrol_grupo_07.remote.MovimientoApi
import com.example.appstockcontrol_grupo_07.remote.MovimientoRemotoDto
import com.example.appstockcontrol_grupo_07.remote.RegistrarEntradaRemoteRequest
import com.example.appstockcontrol_grupo_07.remote.RegistrarSalidaRemoteRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MovimientoInventarioRepositoryTest {

    private fun dummyDto() = MovimientoRemotoDto(
        id = 1,
        productoId = 10,
        tipo = "ENTRADA",
        cantidad = 1,
        stockAnterior = 0,
        stockNuevo = 1,
        usuarioResponsable = "u",
        motivo = "m",
        fechaHora = "2025-12-17T00:00:00"
    )

    @Test
    fun ajuste_si_stock_sube_llama_registrarEntrada_con_abs_y_motivo_prefijo() = runTest {
        val api = mockk<MovimientoApi>()
        val entradaSlot = slot<RegistrarEntradaRemoteRequest>()

        coEvery { api.registrarEntrada(capture(entradaSlot), any(), any()) } returns dummyDto()

        val repo = MovimientoInventarioRepository(movimientoDao = null, api = api)

        val mov = MovimientoInventario(
            productoId = 10,
            tipo = TipoMovimiento.AJUSTE,
            cantidad = -7, // intencional: repo usa abs() ✅
            usuario = "admin@mail.com",
            motivo = "conteo",
            stockAnterior = 10,
            stockNuevo = 17
        )

        repo.registrarMovimiento(mov)

        // Debe llamar ENTRADA
        coVerify(exactly = 1) { api.registrarEntrada(any(), 10, 17) }
        coVerify(exactly = 0) { api.registrarSalida(any(), any(), any()) }

        // Debe usar abs(cantidad) y prefijar el motivo
        assertEquals(7, entradaSlot.captured.cantidad)
        assertEquals("[AJUSTE] conteo", entradaSlot.captured.motivo)
    }

    @Test
    fun ajuste_si_stock_baja_llama_registrarSalida_con_abs_y_motivo_prefijo() = runTest {
        val api = mockk<MovimientoApi>()
        val salidaSlot = slot<RegistrarSalidaRemoteRequest>()

        coEvery { api.registrarSalida(capture(salidaSlot), any(), any()) } returns dummyDto()

        val repo = MovimientoInventarioRepository(movimientoDao = null, api = api)

        val mov = MovimientoInventario(
            productoId = 10,
            tipo = TipoMovimiento.AJUSTE,
            cantidad = -3, // abs() => 3
            usuario = "admin@mail.com",
            motivo = "conteo",
            stockAnterior = 10,
            stockNuevo = 7
        )

        repo.registrarMovimiento(mov)

        // Debe llamar SALIDA
        coVerify(exactly = 1) { api.registrarSalida(any(), 10, 7) }
        coVerify(exactly = 0) { api.registrarEntrada(any(), any(), any()) }

        assertEquals(3, salidaSlot.captured.cantidad)
        assertEquals("[AJUSTE] conteo", salidaSlot.captured.motivo)
    }
}
