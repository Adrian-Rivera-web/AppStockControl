// ProductoViewModel.kt
package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.model.Producto

class ProductoViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            productoRepository.obtenerProductos().collect { listaProductos ->
                _productos.value = listaProductos
            }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            productoRepository.agregarProducto(producto)
            // No necesitamos actualizar manualmente porque el Flow se actualiza automáticamente
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            productoRepository.eliminarProducto(id)
            // No necesitamos actualizar manualmente porque el Flow se actualiza automáticamente
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            productoRepository.actualizarProducto(producto)
            // No necesitamos actualizar manualmente porque el Flow se actualiza automáticamente
        }
    }
}