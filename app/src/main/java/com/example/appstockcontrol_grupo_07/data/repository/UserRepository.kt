package com.example.appstockcontrol_grupo_07.data.repository

import com.example.appstockcontrol_grupo_07.data.local.user.UserDao
import com.example.appstockcontrol_grupo_07.data.local.user.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        println("DEBUG: UserRepository - Iniciando login para: $email")

        val user = userDao.getByEmail(email)
        println("DEBUG: UserRepository - Usuario encontrado: ${user?.email}")
        println("DEBUG: UserRepository - isAdmin del usuario: ${user?.isAdmin}")

        if (user == null) {
            println("DEBUG: UserRepository - ERROR: Usuario no encontrado")
            return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        }

        println("DEBUG: UserRepository - Comparando contraseñas...")
        println("DEBUG: UserRepository - Contraseña ingresada: $password")
        println("DEBUG: UserRepository - Contraseña en BD: ${user.password}")

        return if (user.password == password) {
            println("DEBUG: UserRepository - ✅ LOGIN EXITOSO - Usuario: ${user.email}, isAdmin: ${user.isAdmin}")
            Result.success(user)
        } else {
            println("DEBUG: UserRepository - ❌ LOGIN FALLIDO - Contraseña incorrecta")
            Result.failure(IllegalArgumentException("Contraseña incorrecta"))
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
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password,
                address = address,
                isAdmin = isAdmin
            )
        )
        return Result.success(id)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getByEmail(email)
    }

    suspend fun getUserCount(): Int {
        return userDao.count()
    }

    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAll()
    }

    // ✅ AGREGAR ESTA FUNCIÓN PARA ELIMINAR USUARIOS
    suspend fun deleteUser(userId: Long) {
        userDao.deleteUser(userId)
    }
}