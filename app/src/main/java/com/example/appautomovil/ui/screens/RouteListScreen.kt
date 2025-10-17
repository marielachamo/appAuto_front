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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appautomovil.ui.viewmodel.RutasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(navController: NavController) {
    // 🔹 ViewModel para obtener datos del backend
    val viewModel: RutasViewModel = viewModel()
    val rutas by viewModel.rutas.collectAsState()

    // 🔹 Estado del buscador
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // 🚀 Cargar rutas desde el backend al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarRutas()
    }

    // 🔹 Filtrar rutas según el texto del buscador
    val filteredRoutes = rutas.filter { ruta ->
        ruta.nombreRuta?.contains(searchQuery.text, ignoreCase = true) == true ||
                ruta.linea?.descripcion?.contains(searchQuery.text, ignoreCase = true) == true
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
            // 🔎 Campo de búsqueda
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
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF3E3E4A),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color(0xFF3E3E4A)
                )
            )

            // 🚍 Lista dinámica de rutas del backend
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRoutes) { ruta ->
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
                                    ruta.nombreRuta ?: "Ruta sin nombre",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB2B3FF),
                                    fontSize = 16.sp
                                )
                                Text(
                                    ruta.linea?.descripcion ?: "Sin descripción",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            // ➤ Botón lateral
                            IconButton(onClick = {
                                // Ejemplo: al pulsar puedes navegar al mapa con esta ruta
                                // navController.navigate("mapScreen")
                                navController.navigate("mapScreen/${ruta.idRuta}")
                            }) {
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
