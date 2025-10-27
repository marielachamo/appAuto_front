package com.example.appautomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Linea
import com.example.appautomovil.data.models.Horario
import com.example.appautomovil.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class LineasViewModel : ViewModel() {

    private val repository = MainRepository()

    // 🔹 Estado de todas las líneas
    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas

    // 🔹 Estado de una sola línea (detalle)
    private val _lineaDetalle = MutableStateFlow<Linea?>(null)
    val lineaDetalle: StateFlow<Linea?> = _lineaDetalle

    // 🔹 Estado de horarios
    private val _horarios = MutableStateFlow<List<Horario>>(emptyList())
    val horarios: StateFlow<List<Horario>> = _horarios

    // ✅ Cargar todas las líneas
    fun cargarLineas() {
        viewModelScope.launch {
            try {
                val listaLineas = repository.getLineas()
                _lineas.value = listaLineas
                Log.d("Retrofit", "✅ Líneas recibidas: ${listaLineas.size}")
            } catch (e: Exception) {
                Log.e("Retrofit", "❌ Error al cargar líneas: ${e.message}", e)
            }
        }
    }

    // ✅ Cargar una línea específica por su ID

    fun cargarLineaPorId(idLinea: Int) {
        viewModelScope.launch {
            try {
                val linea = repository.getLineaPorId(idLinea)
                _lineaDetalle.value = linea
            } catch (e: Exception) {
                Log.e("LineaDetalle", "❌ Error al cargar línea: ${e.message}", e)
            }
        }
    }

    // ✅ Cargar todos los horarios
    fun cargarHorarios() {
        viewModelScope.launch {
            try {
                val listaHorarios = repository.getHorarios()
                _horarios.value = listaHorarios
                Log.d("Retrofit", "✅ Horarios recibidos: ${listaHorarios.size}")
            } catch (e: Exception) {
                Log.e("Retrofit", "❌ Error al cargar horarios: ${e.message}", e)
            }
        }
    }
}
