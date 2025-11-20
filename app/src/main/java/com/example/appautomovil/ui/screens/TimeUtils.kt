// Archivo: app/src/main/java/com/example/appautomovil/TimeUtils.kt
package com.example.appautomovil.ui.screens

import java.time.LocalTime
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {
    fun isHorarioActivo(
        horaInicioStr: String?,
        horaFinStr: String?,
        diaSemanaStr: String?
    ): Boolean {
        // 1. Obtener la hora y día actual de Bolivia (GMT-4)
        val zonaBolivia = ZoneId.of("America/La_Paz")
        val ahora = ZonedDateTime.now(zonaBolivia)
        val horaActual = ahora.toLocalTime()
        val diaActual = ahora.dayOfWeek

        // 2. Verificar si el día actual está cubierto
        val isDiaValido = when (diaSemanaStr?.trim()?.lowercase()) {
            "lunes a viernes" -> diaActual >= DayOfWeek.MONDAY && diaActual <= DayOfWeek.FRIDAY
            "sabado" -> diaActual == DayOfWeek.SATURDAY
            "domingo" -> diaActual == DayOfWeek.SUNDAY
            // Añade más casos si tienes otros rangos de días
            else -> false
        }

        if (!isDiaValido) return false

        // 3. Parsear las horas de inicio y fin
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            val inicio = LocalTime.parse(horaInicioStr, formatter)
            val fin = LocalTime.parse(horaFinStr, formatter)

            // 4. Lógica de comparación de tiempo
            if (inicio.isBefore(fin)) {
                // Caso normal (ej: 18:00 a 22:00)
                !horaActual.isBefore(inicio) && horaActual.isBefore(fin)
            } else {
                // Caso que cruza la medianoche (ej: 22:00 a 02:00)
                !horaActual.isBefore(inicio) || horaActual.isBefore(fin)
            }
        } catch (e: Exception) {
            // En caso de error de parseo (si las horas vienen null o mal formadas)
            false
        }
    }
}