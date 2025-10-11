package com.example.appautomovil.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isSatellite by remember { mutableStateOf(false) }
    var showLocation by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }
    var locationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Permiso de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Ubicación activada ", Toast.LENGTH_SHORT).show()
            showLocation = true
        } else {
            Toast.makeText(context, "Ubicación no activada ", Toast.LENGTH_SHORT).show()
        }
    }

    // Configuración base de osmdroid
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    Box(modifier = Modifier.fillMaxSize()) {

        //  Mapa principal
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    val controller = controller
                    val cochabamba = GeoPoint(-17.3895, -66.1568)
                    controller.setZoom(14.0)
                    controller.setCenter(cochabamba)

                    // Marcador base
                    val marker = Marker(this)
                    marker.position = cochabamba
                    marker.title = "Cochabamba Centro"
                    overlays.add(marker)
                }
            },
            update = { mapView ->
                // Cambiar tipo de mapa
                if (isSatellite) {
                    mapView.setTileSource(
                        XYTileSource(
                            "Esri Satellite",
                            0, 19, 256, ".jpg",
                            arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"),
                            "© Esri & Sources"
                        )
                    )
                } else {
                    mapView.setTileSource(TileSourceFactory.MAPNIK)
                }

                // Mostrar ubicación
                if (showLocation) {
                    if (locationOverlay == null) {
                        val overlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                        overlay.enableMyLocation()
                        overlay.enableFollowLocation()
                        mapView.overlays.add(overlay)
                        locationOverlay = overlay
                        overlay.runOnFirstFix {
                            mapView.controller.animateTo(overlay.myLocation)
                        }
                    }
                }

                // Ciclo de vida
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> mapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
            }
        )

        // Barra superior de búsqueda + botones
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .background(Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO: Menú lateral */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú")
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
                IconButton(onClick = { /* TODO: acción de búsqueda */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        }

        //  Botón cambio de vista (satélite / normal)
        FloatingActionButton(
            onClick = { isSatellite = !isSatellite },
            containerColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 120.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = if (isSatellite) Icons.Default.Map else Icons.Default.Satellite,
                contentDescription = "Cambiar vista"
            )
        }

        //  Botón ubicación
        FloatingActionButton(
            onClick = { locationOverlay?.enableFollowLocation() },
            containerColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Centrar ubicación")
        }

        //  Diálogo de permiso
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }) { Text("Sí, mostrar ubicación") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No, gracias")
                    }
                },
                title = { Text("Mostrar ubicación actual") },
                text = { Text("¿Deseas que la app muestre tu ubicación actual en el mapa?") }
            )
        }
    }
}
