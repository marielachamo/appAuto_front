package com.example.appautomovil.data.models

data class Linea(
    val idLinea: Int,
    val nombreLinea: String,
    val descripcion: String?,
    val rutas: List<Ruta>?,
    val paradas: List<Parada>?
)
