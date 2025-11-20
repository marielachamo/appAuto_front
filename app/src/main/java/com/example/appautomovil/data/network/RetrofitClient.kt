package com.example.appautomovil.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Sustituye por la IP real de tu PC o usa 10.0.2.2 si estás en el emulador
    private const val BASE_URL = "http://10.16.54.117:55555"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}