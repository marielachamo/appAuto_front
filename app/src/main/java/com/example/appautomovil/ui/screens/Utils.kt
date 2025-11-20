package com.example.appautomovil.utils

import com.google.android.gms.maps.model.LatLng
import android.util.Log // Importar Log para debug

fun String.toLatLngOrNull(): LatLng? {
    // 1. Limpiar y dividir la cadena por coma.
    val parts = this.trim()
        .replace("\\s".toRegex(), "") // Elimina espacios en blanco
        .split(',')

    if (parts.size != 2) return null

    return try {
        // ASUMIMOS que el formato es: LATITUD, LONGITUD
        val lat = parts[0].toDouble()
        val lon = parts[1].toDouble()
        LatLng(lat, lon)
    } catch (e: NumberFormatException) {
        Log.e("MAPA", "Error al parsear LatLng: $this", e)
        null
    }
}
