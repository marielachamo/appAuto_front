package com.example.appautomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.*
import com.example.appautomovil.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class MapaViewModel : ViewModel() {

    private val repository = MainRepository()

    // 🔹 Estado para las paradas
    private val _paradas = MutableStateFlow<List<Parada>>(emptyList())
    val paradas: StateFlow<List<Parada>> = _paradas

    // 🔹 Estado para las coordenadas (de una ruta específica)
    private val _coordenadas = MutableStateFlow<List<CoordenadaRuta>>(emptyList())
    val coordenadas: StateFlow<List<CoordenadaRuta>> = _coordenadas

    // 🔹 Estado para el nombre de la línea seleccionada
    private val _nombreLinea = MutableStateFlow("")
    val nombreLinea: StateFlow<String> = _nombreLinea

    // 🟢 1️⃣ Cargar todas las paradas (modo general)
    fun cargarParadas() {
        viewModelScope.launch {
            try {
                val listaParadas = repository.getParadas()
                _paradas.value = listaParadas

                Log.d("Retrofit", "✅ Paradas recibidas: ${listaParadas.size}")
                listaParadas.forEach {
                    Log.d("Retrofit", "➡️ ${it.nombreParada} - ${it.ubicacion}")
                }

            } catch (e: Exception) {
                Log.e("Retrofit", "❌ Error al cargar paradas: ${e.message}", e)
            }
        }
    }

    // 🚌 2️⃣ Cargar paradas y coordenadas según la ruta seleccionada
    fun cargarDatosPorRutaId(idRuta: Int) {
        viewModelScope.launch {
            try {
                // 1️⃣ Obtener todas las líneas
                val todasLineas = repository.getLineas()

                // 2️⃣ Buscar la línea que contenga la ruta seleccionada
                val lineaSeleccionada = todasLineas.find { linea ->
                    linea.rutas?.any { it.idRuta == idRuta } == true
                }

                if (lineaSeleccionada != null) {
                    // ✅ Guardar nombre de la línea
                    _nombreLinea.value = lineaSeleccionada.nombreLinea ?: "Desconocida"

                    // ✅ Paradas de esa línea
                    _paradas.value = lineaSeleccionada.paradas ?: emptyList()

                    // ✅ Coordenadas solo de esa ruta específica
                    val rutaSeleccionada = lineaSeleccionada.rutas?.find { it.idRuta == idRuta }
                    _coordenadas.value = rutaSeleccionada?.coordenadas ?: emptyList()

                    Log.d("Retrofit", "✅ Línea: ${_nombreLinea.value}")
                    Log.d("Retrofit", "✅ Paradas cargadas: ${_paradas.value.size}")
                    Log.d("Retrofit", "✅ Coordenadas cargadas: ${_coordenadas.value.size}")
                } else {
                    _nombreLinea.value = ""
                    _paradas.value = emptyList()
                    _coordenadas.value = emptyList()
                    Log.w("Retrofit", "⚠️ No se encontró ninguna línea con ruta ID $idRuta")
                }

            } catch (e: Exception) {
                Log.e("Retrofit", "❌ Error al cargar datos por ruta ID: ${e.message}", e)
            }
        }
    }

}

