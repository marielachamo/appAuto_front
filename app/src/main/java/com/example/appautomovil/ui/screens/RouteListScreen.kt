package com.example.appautomovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(navController: NavController) {
    // 🔍 Estado del texto de búsqueda
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // 🔹 Lista original de rutas
    val allRoutes = listOf(
        Pair("132 Pakata", "Bus 132: K’ara K’ara ➜ Pakata"),
        Pair("132 K’ara K’ara", "Bus 132: Pakata ➜ K’ara K’ara"),
        Pair("110 Tiquipaya", "Bus 110: Sud ➜ Tiquipaya"),
        Pair("240 Norte", "Bus 240: Norte ➜ Centro"),
        Pair("222 Cercado", "Bus 222: Cercado ➜ Centro")
    )

    // 🔎 Filtrar rutas según texto ingresado
    val filteredRoutes = allRoutes.filter {
        it.first.contains(searchQuery.text, ignoreCase = true) ||
                it.second.contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar rutas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 🔍 Campo de búsqueda funcional (sin ">" aquí)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar ruta o parada") },
                leadingIcon = {
                    Icon(Icons.Default.DirectionsBus, contentDescription = "Buscar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true,
                //  Corrección final: TextFieldDefaults.colors() universal
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF3E3E4A),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color(0xFF3E3E4A)
                )
            )

            // 📋 Lista de rutas con botones ">"
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRoutes) { route ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E3E4A))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    route.first,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB2B3FF),
                                    fontSize = 16.sp
                                )
                                Text(
                                    route.second,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            // Botón lateral (por ahora sin acción)
                            IconButton(onClick = { /* TODO: acción futura */ }) {
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Ver detalles",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
