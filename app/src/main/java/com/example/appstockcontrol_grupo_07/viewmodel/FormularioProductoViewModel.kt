package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.model.Producto

class FormularioProductoViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioProductoState())
    val uiState: StateFlow<FormularioProductoState> = _uiState

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errores = it.errores.copy(nombre = null)) }
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion, errores = it.errores.copy(descripcion = null)) }
    }

    fun onPrecioChange(precio: String) {
        _uiState.update { it.copy(precio = precio, errores = it.errores.copy(precio = null)) }
    }

    fun onStockChange(stock: String) {
        _uiState.update { it.copy(stock = stock, errores = it.errores.copy(stock = null)) }
    }

    fun onCategoriaChange(categoria: String) {
        _uiState.update { it.copy(categoria = categoria, errores = it.errores.copy(categoria = null)) }
    }

    fun onProveedorChange(proveedor: String) {
        _uiState.update { it.copy(proveedor = proveedor, errores = it.errores.copy(proveedor = null)) }
    }

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val producto = productoRepository.obtenerProductoPorId(id)
                producto?.let {
                    _uiState.update { state ->
                        state.copy(
                            nombre = it.nombre,
                            descripcion = it.descripcion,
                            precio = it.precio.toString(),
                            stock = it.stock.toString(),
                            categoria = it.categoria,
                            proveedor = it.proveedor,
                            cargando = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar producto: ${e.message}", cargando = false) }
            }
        }
    }

    fun guardarProducto(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) return@launch

            _uiState.update { it.copy(cargando = true) }
            try {
                val producto = Producto(
                    nombre = _uiState.value.nombre,
                    descripcion = _uiState.value.descripcion,
                    precio = _uiState.value.precio.toDouble(),
                    stock = _uiState.value.stock.toInt(),
                    categoria = _uiState.value.categoria,
                    proveedor = _uiState.value.proveedor
                )
                productoRepository.agregarProducto(producto)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}", cargando = false) }
            }
        }
    }

    fun actualizarProducto(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!validarFormulario()) return@launch

            _uiState.update { it.copy(cargando = true) }
            try {
                val producto = Producto(
                    id = id,
                    nombre = _uiState.value.nombre,
                    descripcion = _uiState.value.descripcion,
                    precio = _uiState.value.precio.toDouble(),
                    stock = _uiState.value.stock.toInt(),
                    categoria = _uiState.value.categoria,
                    proveedor = _uiState.value.proveedor
                )
                productoRepository.actualizarProducto(producto)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar: ${e.message}", cargando = false) }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val errores = FormularioProductoErrores(
            nombre = if (_uiState.value.nombre.isBlank()) "Nombre es requerido" else null,
            descripcion = if (_uiState.value.descripcion.isBlank()) "Descripción es requerida" else null,
            precio = try {
                if (_uiState.value.precio.isBlank()) "Precio es requerido"
                else if (_uiState.value.precio.toDouble() <= 0) "Precio debe ser mayor a 0"
                else null
            } catch (e: NumberFormatException) {
                "Precio debe ser un número válido"
            },
            stock = try {
                if (_uiState.value.stock.isBlank()) "Stock es requerido"
                else if (_uiState.value.stock.toInt() < 0) "Stock no puede ser negativo"
                else null
            } catch (e: NumberFormatException) {
                "Stock debe ser un número válido"
            },
            categoria = if (_uiState.value.categoria.isBlank()) "Categoría es requerida" else null,
            proveedor = if (_uiState.value.proveedor.isBlank()) "Proveedor es requerido" else null
        )

        _uiState.update { it.copy(errores = errores) }

        return errores.nombre == null &&
                errores.descripcion == null &&
                errores.precio == null &&
                errores.stock == null &&
                errores.categoria == null &&
                errores.proveedor == null
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class FormularioProductoState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val stock: String = "",
    val categoria: String = "",
    val proveedor: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val errores: FormularioProductoErrores = FormularioProductoErrores()
)

data class FormularioProductoErrores(
    val nombre: String? = null,
    val descripcion: String? = null,
    val precio: String? = null,
    val stock: String? = null,
    val categoria: String? = null,
    val proveedor: String? = null
)