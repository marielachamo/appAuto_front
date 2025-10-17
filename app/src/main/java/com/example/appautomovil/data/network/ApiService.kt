package com.example.appautomovil.data.network

import com.example.appautomovil.data.models.*
import retrofit2.http.GET

interface ApiService {
    @GET("/api/rutas")
    suspend fun getRutas(): List<Ruta>

    @GET("/api/paradas")
    suspend fun getParadas(): List<Parada>

    @GET("/api/coordenadas")
    suspend fun getCoordenadas(): List<CoordenadaRuta>

    @GET("/api/lineas")
    suspend fun getLineas(): List<Linea>


}