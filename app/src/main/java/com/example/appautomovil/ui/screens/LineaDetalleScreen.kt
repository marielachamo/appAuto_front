package com.example.appautomovil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appautomovil.ui.viewmodel.LineasViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineaDetalleScreen(
    idLinea: Int,
    navController: NavController,
    viewModel: LineasViewModel
) {
    // ✅ Estado observable de la línea seleccionada
    val linea by viewModel.lineaDetalle.collectAsState()

    // 🚀 Cuando entra a esta pantalla, carga los datos de esa línea desde el backend
    LaunchedEffect(key1 = idLinea) {
        viewModel.cargarLineaPorId(idLinea = idLinea)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = linea?.nombreLinea ?: "Detalle de línea",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3E3E4A)
                )
            )
        }
    ) { padding ->
        if (linea == null) {
            // 🔄 Muestra indicador mientras carga
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFB2B3FF))
            }
        } else {
            // ✅ Línea cargada correctamente
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFF1E1E2A))
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = linea!!.descripcion ?: "Sin descripción",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFCCCCCC),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

// ✅ Tomamos el estado de la primera ruta
                val estadoRuta = linea!!.rutas?.firstOrNull()?.estadoRuta ?: "Desconocido"

                Text(
                    text = "Estado: $estadoRuta",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = if (estadoRuta.equals("ACTIVA", ignoreCase = true)) Color(0xFF81C784) else Color(0xFFE57373),
                    fontWeight = FontWeight.Bold
                )


                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = " Paradas de la línea",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFB2B3FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    items(linea!!.paradas ?: emptyList()) { parada ->
                        // 👇 Estado expandido por cada parada
                        var expanded by remember { mutableStateOf(false) }
                        val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { expanded = !expanded },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D3A)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = parada.nombreParada ?: "Parada sin nombre",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB2B3FF),
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Tipo: ${parada.tipoParada ?: "Desconocido"}",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Estado: ${parada.estadoParada ?: "Desconocido"}",
                                            color = if (parada.estadoParada.equals("ACTIVA", ignoreCase = true))
                                                Color(0xFF81C784) else Color(0xFFE57373),
                                            fontSize = 14.sp
                                        )
                                    }

                                    // 🔽 Flecha giratoria
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = Color(0xFFB2B3FF),
                                            modifier = Modifier.rotate(rotation)
                                        )
                                    }
                                }

                                // 🔽 Contenido expandible animado
                                AnimatedVisibility(visible = expanded) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        // 🧭 Puesto de control
                                        if (!parada.puestosControl.isNullOrEmpty()) {
                                            val pc = parada.puestosControl.first()
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Puesto: ${pc.nombrePuesto}",
                                                color = Color(0xFFB2B3FF),
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Descripción: ${pc.descripcionPc}",
                                                color = Color(0xFFAAAAAA),
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "⏱️ Tiempo de salida: ${pc.tiempoSalida} seg",
                                                color = Color(0xFFAAAAAA),
                                                fontSize = 12.sp
                                            )
                                        }

                                        // ⏰ Horarios
                                        if (!parada.horarios.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "🕓 Horarios:",
                                                color = Color(0xFFB2B3FF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            parada.horarios.forEach { horario ->
                                                Text(
                                                    text = "• ${horario.dia}: ${horario.horaInicio} - ${horario.horaFin}",
                                                    color = Color(0xFFCCCCCC),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
