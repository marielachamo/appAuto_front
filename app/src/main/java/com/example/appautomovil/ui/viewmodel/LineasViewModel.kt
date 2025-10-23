package com.example.appautomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Linea
import com.example.appautomovil.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class LineasViewModel : ViewModel() {
    private val repository = MainRepository()

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas

    fun cargarLineas() {
        viewModelScope.launch {
            try {
                val listaLineas = repository.getLineas()
                _lineas.value = listaLineas
                Log.d("Retrofit", "✅ Lineas recibidas: ${listaLineas.size}")
            } catch (e: Exception) {
                Log.e("Retrofit", "❌ Error al cargar lineas: ${e.message}", e)
            }
        }
    }
}
