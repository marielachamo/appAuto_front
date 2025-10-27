package com.example.appautomovil.data.repository

import com.example.appautomovil.data.models.*
import com.example.appautomovil.data.network.RetrofitClient

class MainRepository {
    private val api = RetrofitClient.instance.create(com.example.appautomovil.data.network.ApiService::class.java)

    suspend fun getRutas(): List<Ruta> = api.getRutas()
    suspend fun getParadas(): List<Parada> = api.getParadas()
    suspend fun getCoordenadas(): List<CoordenadaRuta> = api.getCoordenadas()
    suspend fun getLineas(): List<Linea> = api.getLineas()
    suspend fun getHorarios(): List<Horario> = api.getHorarios()

    // ✅ Versión final: trae la línea con todas sus paradas
    suspend fun getLineaPorId(idLinea: Int): Linea = api.getLineaPorId(idLinea)
}
