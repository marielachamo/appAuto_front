package com.example.appautomovil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Parada
import com.example.appautomovil.data.models.CoordenadaRuta
import com.example.appautomovil.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

                Log.d("MAPA", "✅ Paradas recibidas (modo general): ${listaParadas.size}")
                listaParadas.forEach {
                    Log.d("MAPA", "➡️ ${it.nombreParada} - ${it.ubicacion}")
                }

            } catch (e: Exception) {
                Log.e("MAPA", "❌ Error al cargar paradas: ${e.message}", e)
            }
        }
    }


    // 🚌 2️⃣ Cargar datos por ID de ruta seleccionada
    fun cargarDatosPorRutaId(idRuta: Int) {
        viewModelScope.launch {
            try {
                val todasLineas = repository.getLineas()

                // 🔍 Buscar la línea que contenga la ruta seleccionada
                val lineaSeleccionada = todasLineas.find { linea ->
                    linea.rutas?.any { it.idRuta == idRuta } == true
                }

                if (lineaSeleccionada != null) {
                    // ✅ Nombre de la línea
                    _nombreLinea.value = lineaSeleccionada.nombreLinea ?: "Desconocida"

                    // ✅ Cargar paradas si existen
                    if (!lineaSeleccionada.paradas.isNullOrEmpty()) {
                        _paradas.value = lineaSeleccionada.paradas!!
                        Log.d("MAPA", "✅ Paradas cargadas: ${_paradas.value.size}")
                        _paradas.value.forEach {
                            Log.d("MAPA", "📍 ${it.nombreParada} -> ${it.ubicacion}")
                        }
                    } else {
                        Log.w("MAPA", "⚠️ Línea encontrada pero sin paradas definidas.")
                        _paradas.value = emptyList()
                    }

                    // ✅ Cargar coordenadas opcionales
                    val rutaSeleccionada = lineaSeleccionada.rutas?.find { it.idRuta == idRuta }
                    _coordenadas.value = rutaSeleccionada?.coordenadas ?: emptyList()

                } else {
                    // ⚠️ Si no se encuentra la línea, limpiar estados
                    _nombreLinea.value = ""
                    _paradas.value = emptyList()
                    _coordenadas.value = emptyList()
                    Log.w("MAPA", "⚠️ No se encontró ninguna línea con ruta ID $idRuta")
                }

            } catch (e: Exception) {
                Log.e("MAPA", "❌ Error al cargar datos por ruta ID: ${e.message}", e)
            }
        }
    }
}
