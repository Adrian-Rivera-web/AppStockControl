package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository
import com.example.appstockcontrol_grupo_07.data.repository.CategoriaRepository // ✅ NUEVO

class FormularioProductoViewModelFactory(
    private val productoRepository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository // ✅ NUEVO
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormularioProductoViewModel::class.java)) {
            return FormularioProductoViewModel(productoRepository, categoriaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}