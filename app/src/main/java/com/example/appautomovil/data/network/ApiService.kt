package com.example.appautomovil.data.network

import com.example.appautomovil.data.models.*
import retrofit2.http.GET

interface ApiService {
    @GET("rutas")
    suspend fun getRutas(): List<Ruta>

    @GET("paradas")
    suspend fun getParadas(): List<Parada>

    @GET("coordenadas")
    suspend fun getCoordenadas(): List<CoordenadaRuta>

}