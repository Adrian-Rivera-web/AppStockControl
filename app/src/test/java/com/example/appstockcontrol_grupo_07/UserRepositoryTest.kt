package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.remote.RegistroRequestDto
import com.example.appstockcontrol_grupo_07.remote.UsuarioApi
import com.example.appstockcontrol_grupo_07.remote.UsuarioResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserRepositoryTest {

    @Test
    fun login_ok_mapea_dto_a_userEntity() = runTest {
        val api = mockk<UsuarioApi>()
        val repo = UserRepository(userDao = null, api = api)

        coEvery { api.login(request = any()) } returns UsuarioResponseDto(
            id = 1,
            nombre = "Admin",
            correo = "admin@mail.com",
            telefono = "123",
            direccion = "Casa",
            esAdmin = true
        )

        val result = repo.login(email = "admin@mail.com", password = "123456")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("Admin", user.name)
        assertTrue(user.isAdmin)
    }

    @Test
    fun register_envia_dto_correcto_y_retorna_id() = runTest {
        val api = mockk<UsuarioApi>()
        val repo = UserRepository(userDao = null, api = api)

        val dtoSlot = slot<RegistroRequestDto>()

        coEvery { api.registrar(capture(dtoSlot)) } returns UsuarioResponseDto(
            id = 99,
            nombre = "Juan",
            correo = "juan@mail.com",
            telefono = "999",
            direccion = "Dir",
            esAdmin = false
        )

        val result = repo.register(
            name = "Juan",
            email = "juan@mail.com",
            phone = "999",
            password = "Abcdef1!",
            address = "Dir",
            isAdmin = true // (tu repo igual no lo manda en el dto)
        )

        // Repo debe devolver el ID
        assertTrue(result.isSuccess)
        assertEquals(99L, result.getOrNull())

        // DTO enviado al endpoint
        assertEquals("Juan", dtoSlot.captured.nombre)
        assertEquals("juan@mail.com", dtoSlot.captured.correo)
        assertEquals("999", dtoSlot.captured.telefono)
        assertEquals("Abcdef1!", dtoSlot.captured.clave)
        assertEquals("Dir", dtoSlot.captured.direccion)

        // Punto clave para la defensa: el repo fuerza aceptaTerminos = true
        assertTrue(dtoSlot.captured.aceptaTerminos)
    }
}
