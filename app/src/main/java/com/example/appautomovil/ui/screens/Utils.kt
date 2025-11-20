package com.example.appautomovil.utils

import com.google.android.gms.maps.model.LatLng
import android.util.Log
import kotlin.math.abs

fun String.toLatLngOrNull(): LatLng? {
    try {
        val raw = this.trim()
        // limpiar paréntesis y espacios extras
        val cleaned = raw.replace("(", "").replace(")", "").replace("\\s".toRegex(), "")
        val parts = cleaned.split(",")
        if (parts.size != 2) {
            Log.e("MAPA", "toLatLngOrNull: formato inválido (no 2 partes): '$raw'")
            return null
        }

        val a = parts[1].toDoubleOrNull()
        val b = parts[0].toDoubleOrNull()
        if (a == null || b == null) {
            Log.e("MAPA", "toLatLngOrNull: no son números: '$raw'")
            return null
        }

        fun isLat(v: Double) = v in -90.0..90.0
        fun isLon(v: Double) = v in -180.0..180.0

        val aIsLat_bIsLon = isLat(a) && isLon(b)
        val bIsLat_aIsLon = isLat(b) && isLon(a)

        return when {
            aIsLat_bIsLon -> {
                // formato lat,lon -> correcto
                LatLng(a, b)
            }
            bIsLat_aIsLon -> {
                // formato lon,lat -> invertir
                Log.d("MAPA", "toLatLngOrNull: detectado lon,lat -> swap raw='$raw'")
                LatLng(b, a)
            }
            else -> {
                // ambiguo: heurística local (|lon| > |lat| en tu zona)
                return if (abs(a) > abs(b)) {
                    Log.d("MAPA", "toLatLngOrNull: heurística swap (a parece lon): raw='$raw' -> LatLng(${b},${a})")
                    LatLng(b, a)
                } else {
                    Log.d("MAPA", "toLatLngOrNull: heurística no-swap (a parece lat): raw='$raw' -> LatLng(${a},${b})")
                    LatLng(a, b)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("MAPA", "toLatLngOrNull: excepción al parsear '$this'", e)
        return null
    }
}
