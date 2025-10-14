package com.example.appautomovil.data.models

data class Ruta(
    val idRuta: Int,
    val nombreRuta: String?,
    val estadoRuta: String?,
    val linea: Linea?,
    val coordenadas: List<CoordenadaRuta>?
)