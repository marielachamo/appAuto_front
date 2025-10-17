package com.example.appautomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Parada
import com.example.appautomovil.data.models.CoordenadaRuta
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

    // --------------------------------------------------------
    // 🟢 1️⃣ Cargar todas las paradas (mapa general)
    // --------------------------------------------------------
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


    fun cargarParadasPorRuta(idRuta: Int) {
        viewModelScope.launch {
            try {
                val todasParadas = repository.getParadas()
                // 🧠 Aquí filtras tú manualmente
                val filtradas = todasParadas.filter { parada ->
                    parada.tipoParada?.contains(idRuta.toString()) == true
                }
                _paradas.value = filtradas
            } catch (e: Exception) {
                Log.e("Retrofit", "Error al filtrar paradas por ruta: ${e.message}")
            }
        }
    }

    fun cargarCoordenadasPorRuta(idRuta: Int) {
        viewModelScope.launch {
            try {
                val todasCoordenadas = repository.getCoordenadas()
                val filtradas = todasCoordenadas.filter {
                    it.idCoordenada.toString().contains(idRuta.toString())
                }
                _coordenadas.value = filtradas
            } catch (e: Exception) {
                Log.e("Retrofit", "Error al filtrar coordenadas: ${e.message}")
            }
        }
    }
}
