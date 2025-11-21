package com.example.appautomovil.data.remote


import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object DirectionsApiClient {

    private const val API_KEY = "AIzaSyBOmhIkEof6wEH2DZBy_azKLPc8dr3AjIo" //  pon tu API key aquí

    suspend fun getRoute(origin: LatLng, destination: LatLng): List<LatLng> {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    "https://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=${origin.latitude},${origin.longitude}" +
                            "&destination=${destination.latitude},${destination.longitude}" +
                            "&mode=driving&key=$API_KEY"

                val response = URL(url).readText()
                val json = JSONObject(response)

                val routes = json.getJSONArray("routes")
                if (routes.length() == 0) return@withContext emptyList()

                val points =
                    routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")

                decodePolyline(points)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
