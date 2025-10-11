package com.example.appautomovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToMap: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Movilidad Urbana Cochabamba") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido ", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onNavigateToMap) {
                Text("Ver Mapa de Paradas")
            }
        }
    }
}
