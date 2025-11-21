package com.example.appautomovil.data.remote

import com.google.android.gms.maps.model.LatLng
import retrofit2.http.GET
import retrofit2.http.Query

// ---- DATA CLASSES ----
data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val overview_polyline: OverviewPolyline
)

data class OverviewPolyline(
    val points: String
)

// ---- INTERFACE RETROFIT ----
interface DirectionsApiService {

    @GET("maps/api/directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,        // "lat,lng"
        @Query("destination") destination: String, // "lat,lng"
        @Query("mode") mode: String = "driving",   // o "walking", "transit"
        @Query("key") apiKey: String
    ): DirectionsResponse
}
