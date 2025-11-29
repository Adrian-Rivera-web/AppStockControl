package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.user.UserDao
import com.example.appstockcontrol_grupo_07.data.local.user.UserEntity
import com.example.appstockcontrol_grupo_07.remote.LoginRequestDto
import com.example.appstockcontrol_grupo_07.remote.RegistroRequestDto
import com.example.appstockcontrol_grupo_07.remote.UsuarioApi
import com.example.appstockcontrol_grupo_07.remote.UsuarioResponseDto
import com.example.appstockcontrol_grupo_07.remote.RetrofitInstance

class UserRepository(
    private val userDao: UserDao? = null,
    private val api: UsuarioApi = RetrofitInstance.usuarioApi
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val dto = api.login(
                LoginRequestDto(
                    correo = email,
                    clave = password
                )
            )
            Result.success(dto.toUserEntity())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        address: String,
        isAdmin: Boolean = false
    ): Result<Long> {
        return try {
            val dto = api.registrar(
                RegistroRequestDto(
                    nombre = name,
                    correo = email,
                    telefono = phone,
                    clave = password,
                    direccion = address,
                    aceptaTerminos = true
                )
            )
            Result.success(dto.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): List<UserEntity> {
        val remotos = api.obtenerUsuarios()
        return remotos.map { it.toUserEntity() }
    }

    suspend fun deleteUser(userId: Long) {
        val response = api.eliminarUsuario(userId)
        if (!response.isSuccessful) {
            throw IllegalStateException("Error al eliminar usuario: ${response.code()}")
        }
    }

    suspend fun changePassword(email: String, newPassword: String): String? {
        val dao = userDao ?: return "Cambio de contraseña solo disponible en modo local."

        val user = dao.getByEmail(email) ?: return "Usuario no encontrado."

        if (user.password == newPassword) {
            return "La nueva contraseña no puede ser igual a la anterior."
        }

        dao.updatePassword(email, newPassword)
        return null  // null = todo OK
    }
}

private fun UsuarioResponseDto.toUserEntity(): UserEntity =
    UserEntity(
        id = this.id,
        name = this.nombre,
        email = this.correo,
        phone = this.telefono,
        password = "",
        address = this.direccion,
        isAdmin = this.esAdmin
    )
