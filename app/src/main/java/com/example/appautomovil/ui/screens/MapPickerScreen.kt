package com.example.appautomovil.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapPickerScreen(
    tipo: String, // "origen" o "destino"
    onPointSelected: (LatLng) -> Unit,
    onConfirm: (LatLng) -> Unit,
    onBack: () -> Unit
) {
    // Centro inicial Cochabamba
    val cochabamba = LatLng(-17.3895, -66.1568)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cochabamba, 14f)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (tipo == "origen") "Elegir origen en el mapa" else "Elegir destino en el mapa"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de confirmar (solo activo si ya se eligió un punto)
                    IconButton(
                        onClick = { selectedLatLng?.let { onConfirm(it) } },
                        enabled = selectedLatLng != null
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirmar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                    // notificar al navGraph (SelectLocation) que hubo un punto nuevo
                    onPointSelected(latLng)
                }
            ) {
                selectedLatLng?.let { point ->
                    Marker(
                        state = MarkerState(position = point),
                        title = if (tipo == "origen") "Origen seleccionado" else "Destino seleccionado"
                    )
                }
            }

            // Texto de ayuda
            if (selectedLatLng == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    tonalElevation = 4.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Toca en el mapa para seleccionar el ${if (tipo == "origen") "origen" else "destino"}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
