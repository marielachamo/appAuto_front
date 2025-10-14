package com.example.appautomovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onNavigateToMap: () -> Unit) {
    // Fondo general con color suave
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB)), // fondo claro profesional
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            //  Ícono o logo superior
            Surface(
                shape = CircleShape,
                color = Color(0xFF3E4A7B).copy(alpha = 0.1f),
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsBus, //  sin necesidad de drawable
                    contentDescription = "Logo transporte",
                    tint = Color(0xFF3E4A7B),
                    modifier = Modifier.padding(20.dp)
                )
            }

            // Título principal
            Text(
                text = "Movilidad Urbana Cochabamba",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E3E4A)
            )

            // Subtítulo de bienvenida
            Text(
                text = "Bienvenido",
                fontSize = 18.sp,
                color = Color(0xFF555555)
            )

            //  Botón principal
            Button(
                onClick = onNavigateToMap,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E4A7B),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(220.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Ver Mapa de Paradas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
