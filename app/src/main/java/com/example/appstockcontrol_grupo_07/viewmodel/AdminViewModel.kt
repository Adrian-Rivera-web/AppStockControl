package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.UserRepository
import com.example.appstockcontrol_grupo_07.data.local.user.UserEntity // ← AGREGAR ESTA IMPORTACIÓN

class AdminViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersState())
    val uiState: StateFlow<AdminUsersState> = _uiState

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val usuarios = userRepository.getAllUsers()
                _uiState.update {
                    it.copy(
                        usuarios = usuarios,
                        cargando = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar usuarios: ${e.message}",
                        cargando = false
                    )
                }
            }
        }
    }

    fun eliminarUsuario(userId: Long) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                // Recargar la lista después de eliminar
                cargarUsuarios()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al eliminar usuario: ${e.message}"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class AdminUsersState(
    val usuarios: List<UserEntity> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)