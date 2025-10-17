package com.example.appautomovil.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appautomovil.ui.viewmodel.MapaViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, rutaId: Int? = null) {

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showSatellite by remember { mutableStateOf(false) }

    // 🧠 ViewModel (para datos del backend)
    val viewModel: MapaViewModel = viewModel()
    val paradas by viewModel.paradas.collectAsState()

    // 🚀 Cargar paradas al iniciar
    LaunchedEffect(rutaId) {
        if (rutaId != null) {
            // Cargar paradas y coordenadas de esta ruta
            viewModel.cargarParadasPorRuta(rutaId)
            viewModel.cargarCoordenadasPorRuta(rutaId)
        } else {
            // Si no hay rutaId, solo carga todo normalmente
            viewModel.cargarParadas()
        }
    }


    // 📍 Cliente de ubicación y estado actual
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // 🔑 Permiso de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // 🌍 Coordenadas iniciales (Cochabamba)
    val cochabamba = LatLng(-17.3895, -66.1568)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cochabamba, 15f)
    }

    // 🚀 Detectar ubicación actual automáticamente
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                userLocation = LatLng(it.latitude, it.longitude)
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(userLocation!!, 16f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🗺️ Mapa principal
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                mapType = if (showSatellite) MapType.SATELLITE else MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true
            ),
            cameraPositionState = cameraPositionState
        ) {
            // 📍 Marcador inicial de referencia
            Marker(
                state = MarkerState(position = cochabamba),
                title = "Cochabamba Centro",
                snippet = "Punto de inicio"
            )

            // 📍 Marcadores de las paradas desde el backend
            paradas.forEach { parada ->
                parada.ubicacion?.let { ubicacionStr ->
                    val partes = ubicacionStr.split(",")
                    if (partes.size == 2) {
                        val lat = partes[0].trim().toDoubleOrNull()
                        val lon = partes[1].trim().toDoubleOrNull()
                        if (lat != null && lon != null) {
                            Marker(
                                state = MarkerState(position = LatLng(lat, lon)),
                                title = parada.nombreParada ?: "Parada sin nombre",
                                snippet = "Parada del transporte público"
                            )
                        }
                    }
                }
            }

            // 📍 Marcador de la ubicación actual
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Tu ubicación actual",
                    snippet = "Estás aquí")
            }
        }

        // 🔝 Barra superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .background(Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("routeList") }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú rutas")
                }

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar ruta o parada") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(onClick = { viewModel.cargarParadas() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar mapa")
                }
            }
        }

        // ⚙️ Controles personalizados (vista, zoom, ubicación)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showSatellite = !showSatellite },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(55.dp)
            ) {
                Icon(
                    imageVector = if (showSatellite) Icons.Default.Map else Icons.Default.Satellite,
                    contentDescription = "Cambiar vista"
                )
            }

            FloatingActionButton(
                onClick = {
                    val newZoom = cameraPositionState.position.zoom + 1
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        cameraPositionState.position.target,
                        newZoom
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Acercar mapa")
            }

            FloatingActionButton(
                onClick = {
                    val newZoom = cameraPositionState.position.zoom - 1
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        cameraPositionState.position.target,
                        newZoom
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Alejar mapa")
            }

            FloatingActionButton(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            userLocation = LatLng(it.latitude, it.longitude)
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(userLocation!!, 16f)
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(55.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
            }
        }
    }
}

