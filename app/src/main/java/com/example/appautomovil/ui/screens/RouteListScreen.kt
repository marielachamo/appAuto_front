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
import com.example.appautomovil.ui.viewmodel.LineasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(navController: NavController) {
    // ✅ ViewModel correcto
    val viewModel: LineasViewModel = viewModel()
    val lineas by viewModel.lineas.collectAsState()

    // 🔎 Estado del buscador
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // 🚀 Cargar líneas desde el backend al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarLineas()
    }

    // 🔍 Filtrar líneas según texto del buscador
    val filteredLineas = lineas.filter { linea ->
        linea.nombreLinea?.contains(searchQuery.text, ignoreCase = true) == true ||
                linea.descripcion?.contains(searchQuery.text, ignoreCase = true) == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar líneas") },
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
            // 🔍 Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar línea o descripción") },
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

            // 🚍 Lista de líneas obtenidas desde el backend
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredLineas) { linea ->
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
                                // 🟣 Mostrar nombre de la línea (por ejemplo: Línea 110)
                                Text(
                                    linea.nombreLinea ?: "Línea sin nombre",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB2B3FF),
                                    fontSize = 16.sp
                                )
                                // 🔹 Mostrar descripción debajo
                                Text(
                                    linea.descripcion ?: "",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            // ➤ Botón lateral para abrir el mapa
                            IconButton(onClick = {
                                val idRuta = linea.rutas?.firstOrNull()?.idRuta
                                if (idRuta != null) {
                                    navController.navigate("mapScreen/$idRuta")
                                }
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
