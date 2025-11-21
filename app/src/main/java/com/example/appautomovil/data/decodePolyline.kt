package com.example.appautomovil.data.remote

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

/**
 * Decodifica una polyline del Directions API a una lista de LatLng.
 */
fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val latDecoded = lat / 1E5
        val lngDecoded = lng / 1E5

        poly.add(LatLng(latDecoded, lngDecoded))
    }

    return poly
}
