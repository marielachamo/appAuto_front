package com.example.appautomovil.data.models

data class Parada(
    val idParada: Int,
    val nombreParada: String?,
    val tipoParada: String?,
    val estadoParada: String?,
    val ubicacion: String?,
    val linea: Linea? = null,
    val horarios: List<Horario>?,
    val puestosControl: List<PuestoControl>?
)