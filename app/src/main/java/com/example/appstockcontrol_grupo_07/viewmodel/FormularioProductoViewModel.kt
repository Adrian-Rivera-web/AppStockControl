package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository // ✅ NUEVO
import com.example.appstockcontrol_grupo_07.model.Producto
import com.example.appstockcontrol_grupo_07.validation.Validators
import com.example.appstockcontrol_grupo_07.validation.ValidationResult

class FormularioProductoViewModel(
    private val productoRepository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository // ✅ NUEVO: Recibir el repositorio de categorías
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioProductoState())
    val uiState: StateFlow<FormularioProductoState> = _uiState

    // ✅ NUEVO: Lista de categorías existentes
    private val _categoriasExistentes = MutableStateFlow<List<String>>(emptyList())
    val categoriasExistentes: StateFlow<List<String>> = _categoriasExistentes

    init {
        cargarCategoriasExistentes() // ✅ Cargar categorías al iniciar
    }

    // ✅ NUEVO: Cargar categorías existentes
    private fun cargarCategoriasExistentes() {
        viewModelScope.launch {
            categoriaRepository.obtenerCategorias().collect { categorias ->
                _categoriasExistentes.value = categorias
                    .filter { it.activa } // Solo categorías activas
                    .map { it.nombre }
                    .sorted()
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errores = it.errores.copy(nombre = null)) }
    }

    fun onImagenUriChange(nuevoUri: String) {
        _uiState.update { it.copy(imagenUri = nuevoUri) }
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
        _uiState.update {
            it.copy(
                categoria = categoria,
                errores = it.errores.copy(categoria = null),
                mostrarSugerencias = categoria.isNotEmpty() // ✅ Mostrar sugerencias cuando se escribe
            )
        }
    }

    fun onProveedorChange(proveedor: String) {
        _uiState.update { it.copy(proveedor = proveedor, errores = it.errores.copy(proveedor = null)) }
    }

    // ✅ NUEVO: Seleccionar categoría de las sugerencias
    fun seleccionarCategoria(categoria: String) {
        _uiState.update {
            it.copy(
                categoria = categoria,
                mostrarSugerencias = false
            )
        }
    }

    // ✅ NUEVO: Ocultar sugerencias
    fun ocultarSugerencias() {
        _uiState.update { it.copy(mostrarSugerencias = false) }
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
                            stockMinimo = it.stockMinimo.toString(),
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
                val state = _uiState.value   // 👈 tomamos un snapshot del estado

                val producto = Producto(
                    nombre = state.nombre.trim(),
                    descripcion = state.descripcion.trim(),
                    precio = state.precio.toDouble(),
                    stock = state.stock.toInt(),
                    stockMinimo = state.stockMinimo.toIntOrNull() ?: 0,
                    categoria = state.categoria.trim(),
                    proveedor = state.proveedor.trim(),
                    imagenUri = state.imagenUri      // 👈 AHORA SÍ: viene desde el state
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
                val state = _uiState.value

                val producto = Producto(
                    id = id,
                    nombre = state.nombre.trim(),
                    descripcion = state.descripcion.trim(),
                    precio = state.precio.toDouble(),
                    stock = state.stock.toInt(),
                    stockMinimo = state.stockMinimo.toIntOrNull() ?: 0,
                    categoria = state.categoria.trim(),
                    proveedor = state.proveedor.trim(),
                    imagenUri = state.imagenUri      // 👈 también aquí, para no perder la imagen
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
            nombre = when (val result = Validators.validateNonEmpty("Nombre", _uiState.value.nombre)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            descripcion = when (val result = Validators.validateNonEmpty("Descripción", _uiState.value.descripcion)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            precio = when (val result = Validators.validatePrice(_uiState.value.precio)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            stock = when (val result = Validators.validateStock(_uiState.value.stock)) {
                is ValidationResult.Error -> result.message
                else -> null
            },
            stockMinimo = when {
                _uiState.value.stockMinimo.isBlank() -> null   // lo dejamos opcional
                else -> when (val result = Validators.validateStock(_uiState.value.stockMinimo)) {
                    is ValidationResult.Error -> result.message
                    else -> null
                }
            },
            categoria = when {
                _uiState.value.categoria.isBlank() -> "Categoría es requerida"
                !_categoriasExistentes.value.any { it.equals(_uiState.value.categoria, ignoreCase = true) } ->
                    "❌ Esta categoría no existe. Categorías válidas: ${_categoriasExistentes.value.joinToString(", ")}"
                else -> null
            },
            proveedor = when (val result = Validators.validateNonEmpty("Proveedor", _uiState.value.proveedor)) {
                is ValidationResult.Error -> result.message
                else -> null
            }

        )

        _uiState.update { it.copy(errores = errores) }

        return errores.nombre == null &&
                errores.descripcion == null &&
                errores.precio == null &&
                errores.stock == null &&
                errores.stockMinimo == null &&
                errores.categoria == null &&
                errores.proveedor == null
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
    fun onStockMinimoChange(stockMinimo: String) {
        _uiState.update {
            it.copy(
                stockMinimo = stockMinimo,
                errores = it.errores.copy(stockMinimo = null)
            )
        }
    }

}

data class FormularioProductoState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val stock: String = "",
    val stockMinimo: String = "",
    val categoria: String = "",
    val proveedor: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val errores: FormularioProductoErrores = FormularioProductoErrores(),
    val mostrarSugerencias: Boolean = false, // ✅ NUEVO: Controlar visibilidad de sugerencias
    val imagenUri: String? = null
)

data class FormularioProductoErrores(
    val nombre: String? = null,
    val descripcion: String? = null,
    val precio: String? = null,
    val stock: String? = null,
    val stockMinimo: String? = null,
    val categoria: String? = null,
    val proveedor: String? = null
)