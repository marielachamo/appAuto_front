package com.example.appautomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appautomovil.data.models.Ruta
import com.example.appautomovil.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log // 👈 Importante para ver logs en Logcat

class RutasViewModel : ViewModel() {
    private val repository = MainRepository()

    private val _rutas = MutableStateFlow<List<Ruta>>(emptyList())
    val rutas: StateFlow<List<Ruta>> = _rutas

    fun cargarRutas() {
        viewModelScope.launch {
            try {
                val listaRutas = repository.getRutas()

                // ✅ Guardar las rutas recibidas
                _rutas.value = listaRutas

                // 📋 Mostrar en Logcat
                Log.d("Retrofit", "✅ Rutas recibidas: ${listaRutas.size}")
                listaRutas.forEach {
                    Log.d("Retrofit", "➡️ ${it.idRuta} - ${it.nombreRuta}")
                }

            } catch (e: Exception) {
                Log.e("Retrofit", "❌ Error al cargar rutas: ${e.message}", e)
            }
        }
    }
}

