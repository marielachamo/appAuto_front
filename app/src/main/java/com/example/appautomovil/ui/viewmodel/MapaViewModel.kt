package com.example.appautomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Parada
import com.example.appautomovil.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log  //  Importante para ver logs en Logcat

class MapaViewModel : ViewModel() {
    private val repository = MainRepository()

    private val _paradas = MutableStateFlow<List<Parada>>(emptyList())
    val paradas: StateFlow<List<Parada>> = _paradas

    fun cargarParadas() {
        viewModelScope.launch {
            try {
                val listaParadas = repository.getParadas()

                //  Asigna el resultado al flujo
                _paradas.value = listaParadas

                //  Log para confirmar conexión
                Log.d("Retrofit", " Conectado al backend. Paradas recibidas: ${listaParadas.size}")

                listaParadas.forEach {
                    Log.d("Retrofit", " ${it.nombreParada} - ${it.ubicacion}")
                }

            } catch (e: Exception) {
                Log.e("Retrofit", "Error al cargar paradas: ${e.message}", e)
            }
        }
    }
}
