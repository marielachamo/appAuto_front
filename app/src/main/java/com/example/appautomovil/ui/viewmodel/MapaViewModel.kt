package com.example.appautomovil.ui.viewmodel

import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Parada
import com.example.appautomovil.data.models.CoordenadaRuta
import com.example.appautomovil.data.repository.MainRepository
import com.example.appautomovil.utils.toLatLngOrNull //
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val BOLIVIA_BOUNDS = LatLngBounds(
    LatLng(-23.0, -70.0), // Suroeste de Bolivia
    LatLng(-9.0, -57.0)   // Noreste de Bolivia
)
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
    private val _ubicacionBuscada = MutableStateFlow<LatLng?>(null)
    val ubicacionBuscada: StateFlow<LatLng?> = _ubicacionBuscada
    private val _paradasCercanas = MutableStateFlow<List<Parada>>(emptyList())
    val paradasCercanas: StateFlow<List<Parada>> = _paradasCercanas


    // 🟢 1️⃣ Cargar todas las paradas (modo general)
    fun cargarParadas() {
        viewModelScope.launch {
            try {
                val listaParadas = repository.getParadas()
                _paradas.value = listaParadas
                Log.d("MAPA", "✅ Paradas recibidas (modo general): ${listaParadas.size}")
            } catch (e: Exception) {
                Log.e("MAPA", "❌ Error al cargar paradas: ${e.message}", e)
            }
        }
    }


    // 🚌 2️⃣ Cargar datos por ID de ruta seleccionada
    fun cargarDatosPorRutaId(idRuta: Int) {
        // ... (Tu código existente aquí, sin cambios)
        viewModelScope.launch {
            try {
                val todasLineas = repository.getLineas()

                // 🔍 Buscar la línea que contenga la ruta seleccionada
                val lineaSeleccionada = todasLineas.find { linea ->
                    linea.rutas?.any { it.idRuta == idRuta } == true
                }

                if (lineaSeleccionada != null) {
                    _nombreLinea.value = lineaSeleccionada.nombreLinea ?: "Desconocida"

                    if (!lineaSeleccionada.paradas.isNullOrEmpty()) {
                        _paradas.value = lineaSeleccionada.paradas!!
                    } else {
                        _paradas.value = emptyList()
                    }

                    val rutaSeleccionada = lineaSeleccionada.rutas?.find { it.idRuta == idRuta }
                    _coordenadas.value = rutaSeleccionada?.coordenadas ?: emptyList()

                } else {
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

    // 🗺️ 3️⃣ Cargar paradas por ID de línea seleccionada
    fun cargarParadasPorLinea(idLinea: Int) {
        viewModelScope.launch {
            try {
                val todasLineas = repository.getLineas()
                val lineaSeleccionada = todasLineas.find { it.idLinea == idLinea }

                if (lineaSeleccionada != null) {
                    _nombreLinea.value = lineaSeleccionada.nombreLinea
                    _paradas.value = lineaSeleccionada.paradas ?: emptyList()
                    _coordenadas.value =
                        lineaSeleccionada.rutas?.firstOrNull()?.coordenadas ?: emptyList()
                    Log.d("MAPA", "✅ Coordenadas cargadas: ${_coordenadas.value.size}")
                } else {
                    _nombreLinea.value = ""
                    _paradas.value = emptyList()
                    _coordenadas.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("MAPA", "❌ Error al cargar paradas por línea: ${e.message}", e)
            }
        }
    }

    // --- 🚨 NUEVA FUNCIÓN PARA BÚSQUEDA DE CALLES (GEOCODIFICACIÓN) ---
    fun buscarDireccion(context: Context, address: String) {
        if (address.isBlank()) return

        // Usamos la implementación adecuada según la versión del SDK de Android.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val geocoder = Geocoder(context)

            // Método asíncrono y moderno (API 33+) usando los límites geográficos
            geocoder.getFromLocationName(
                address,
                1,
                BOLIVIA_BOUNDS.southwest.latitude,
                BOLIVIA_BOUNDS.southwest.longitude,
                BOLIVIA_BOUNDS.northeast.latitude,
                BOLIVIA_BOUNDS.northeast.longitude,
                object : GeocodeListener {
                    override fun onGeocode(results: List<android.location.Address>) {
                        if (results.isNotEmpty()) {
                            val location = results[0]
                            val latLng = LatLng(location.latitude, location.longitude)

                            // 1. Centrar Mapa
                            _ubicacionBuscada.value = latLng

                            // 2. 🚨 LLAMADA CLAVE: Filtrar paradas cercanas
                            filtrarParadasPorProximidad(latLng) // <-- ¡Añadido!

                            Log.d("MAPA", "✅ Dirección encontrada (API 33+): $latLng")
                        } else {
                            _ubicacionBuscada.value = null
                            _paradasCercanas.value = emptyList() // <-- Limpiar en caso de fallo
                            Log.w("MAPA", "⚠️ No se encontraron resultados en Bolivia.")
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        Log.e("MAPA", "❌ Error de Geocodificación (API 33+): $errorMessage")
                        _ubicacionBuscada.value = null
                        _paradasCercanas.value = emptyList() // <-- Limpiar en caso de error
                    }
                }
            )

        } else {
            // Método síncrono (deprecated) para APIs anteriores
            viewModelScope.launch {
                val geocoder = Geocoder(context)
                try {
                    val results = geocoder.getFromLocationName(
                        address,
                        1,
                        BOLIVIA_BOUNDS.southwest.latitude,
                        BOLIVIA_BOUNDS.southwest.longitude,
                        BOLIVIA_BOUNDS.northeast.latitude,
                        BOLIVIA_BOUNDS.northeast.longitude
                    )

                    if (!results.isNullOrEmpty()) {
                        val location = results[0]
                        val latLng = LatLng(location.latitude, location.longitude)

                        // 1. Centrar Mapa
                        _ubicacionBuscada.value = latLng

                        // 2. 🚨 LLAMADA CLAVE: Filtrar paradas cercanas
                        filtrarParadasPorProximidad(latLng) // <-- ¡Añadido!

                        Log.d("MAPA", "✅ Dirección encontrada (API < 33): $latLng")
                    } else {
                        _ubicacionBuscada.value = null
                        _paradasCercanas.value = emptyList() // <-- Limpiar en caso de fallo
                    }
                } catch (e: Exception) {
                    Log.e("MAPA", "❌ Error de Geocodificación (API < 33): ${e.message}", e)
                    _ubicacionBuscada.value = null
                    _paradasCercanas.value = emptyList() // <-- Limpiar en caso de error
                }
            }
        }
    }
    fun filtrarParadasPorProximidad(centro: LatLng) {
        val radioMetros = 1000.0f // 2 km

        val todasLasParadas = _paradas.value // Asume que _paradas ya contiene todas las paradas

        // Función para calcular la distancia en metros entre dos LatLng
        fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
            val results = FloatArray(1)
            // Usa el método estático de la clase Location para calcular distancias geodésicas (más preciso)
            Location.distanceBetween(lat1, lon1, lat2, lon2, results)
            return results[0] // Distancia en metros
        }

        val paradasFiltradas = todasLasParadas.filter { parada ->
            // Asegúrate de que tu extensión 'toLatLngOrNull' esté disponible y funcione
            val paradaLatLng = parada.ubicacion?.toLatLngOrNull()

            if (paradaLatLng != null) {
                val distance = calculateDistance(
                    centro.latitude, centro.longitude,
                    paradaLatLng.latitude, paradaLatLng.longitude
                )
                // Retorna true si la distancia es menor o igual al radio (2000 metros)
                distance <= radioMetros
            } else {
                false
            }
        }

        _paradasCercanas.value = paradasFiltradas
        Log.d("MAPA", "✅ Se encontraron ${paradasFiltradas.size} paradas cerca de la ubicación buscada.")
    }
    fun limpiarResultadosBusqueda() {
        _ubicacionBuscada.value = null
        _paradasCercanas.value = emptyList()
    }
}