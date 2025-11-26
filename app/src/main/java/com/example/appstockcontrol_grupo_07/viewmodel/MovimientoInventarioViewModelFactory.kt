package com.example.appstockcontrol_grupo_07.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appstockcontrol_grupo_07.data.repository.MovimientoInventarioRepository
import com.example.appstockcontrol_grupo_07.data.repository.ProductoRepository

class MovimientoInventarioViewModelFactory(
    private val movimientoRepo: MovimientoInventarioRepository,
    private val productoRepo: ProductoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovimientoInventarioViewModel::class.java)) {
            return MovimientoInventarioViewModel(movimientoRepo, productoRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
