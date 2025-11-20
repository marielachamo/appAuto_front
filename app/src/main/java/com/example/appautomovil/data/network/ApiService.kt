package com.example.appautomovil.data.network

import com.example.appautomovil.data.models.*
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/rutas")
    suspend fun getRutas(): List<Ruta>

    @GET("/api/paradas")
    suspend fun getParadas(): List<Parada>

    @GET("/api/coordenadas")
    suspend fun getCoordenadas(): List<CoordenadaRuta>

    @GET("/api/lineas")
    suspend fun getLineas(): List<Linea>

    @GET("/api/horarios")
    suspend fun getHorarios(): List<Horario>

    // NUEVO: obtener una línea completa por su ID
    @GET("/api/lineas/{id}")
    suspend fun getLineaPorId(@Path("id") id: Int): Linea

    @GET("/api/lineas/rutas/{coord}")
    suspend fun getLineasPorCoordenada(
        @Path("coord") coord: String
    ): List<Linea>

    // Nuevo: Buscar líneas por dos puntos (lat$lon / lat$lon)
    @GET("/api/lineas/rutas/dual/{coord1}/{coord2}")
    suspend fun getLineasPorDosCoordenadas(
        @Path("coord1") coord1: String,
        @Path("coord2") coord2: String
    ): List<Linea>
}
