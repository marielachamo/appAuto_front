package com.example.appautomovil.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Importación necesaria para el SuggestionItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex // Importación necesaria para la capa del buscador
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appautomovil.ui.viewmodel.*
import com.example.appautomovil.data.models.*
import com.example.appautomovil.utils.toLatLngOrNull
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.asStateFlow


// MapScreen.kt (arriba del archivo, fuera de cualquier @Composable)
/*private fun String.toLatLngOrNull(): LatLng? {
    val cleaned = this.replace("(", "").replace(")", "")
    val parts = cleaned.split(",")
    if (parts.size != 2) return null
    val lon = parts[0].trim().toDoubleOrNull() ?: return null
    val lat = parts[1].trim().toDoubleOrNull() ?: return null
    return LatLng(lat, lon) // LatLng(lat, lon)
}*/
/**
 * Función reutilizable para dibujar paradas y rutas de una línea en el mapa
 * @param linea Objeto Linea con paradas y rutas
 * @param colorMarker Color para los marcadores en formato String (ej: "#FF0000")
 * @param colorRoute Color para la polyline en formato String (ej: "#2196F3")
 */
@Composable
fun DrawLineaOnMap(
    linea: Linea,
    colorMarker: String = "#2196F3", // Azul por defecto
    colorRoute: String = "#2196F3",  // Azul por defecto
    // Parámetro opcional: coordenadas externas (ej: las que carga MapaViewModel)
    coordenadasExternas: List<com.example.appautomovil.data.models.CoordenadaRuta>? = null
) {
    // 🗺️ Dibujar marcadores de paradas (igual que antes)
    linea.paradas?.forEach { parada ->
        parada.ubicacion?.let { ubicStr ->
            // usa la extensión centralizada si existe
            val latLng = try {
                // intenta la extensión utilitaria primero
                ubicStr.toLatLngOrNull()
            } catch (e: Exception) {
                null
            }

            latLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = parada.nombreParada ?: "Parada",
                    snippet = "🚏 Línea ${linea.nombreLinea}",
                    icon = BitmapDescriptorFactory.defaultMarker(parseColorToHue(colorMarker))
                )
            }
        }
    }

    // 🛣️ 1) Prioridad: coordenadasExternas si vienen
    val puntosDesdeExternas: List<com.google.android.gms.maps.model.LatLng> =
        coordenadasExternas?.mapNotNull { coord ->
            coord.coordenada?.toLatLngOrNull()
        } ?: emptyList()

    // 🛣️ 2) Si no hay externas, intenta usar ruta.coordenadas dentro de linea (como antes)
    val puntosDesdeLinea: List<com.google.android.gms.maps.model.LatLng> =
        linea.rutas?.flatMap { ruta ->
            ruta.coordenadas?.mapNotNull { c -> c.coordenada?.toLatLngOrNull() } ?: emptyList()
        } ?: emptyList()

    // 🛣️ 3) Fallback: si no hay coordenadas de ruta, construir una polyline con las paradas (orden tal cual vienen)
    val puntosDesdeParadas: List<com.google.android.gms.maps.model.LatLng> =
        linea.paradas?.mapNotNull { parada ->
            parada.ubicacion?.toLatLngOrNull()
        } ?: emptyList()

    // Elegir la mejor fuente disponible (externas > lineas.rutas > paradas)
    val puntosPolyline = when {
        puntosDesdeExternas.size >= 2 -> puntosDesdeExternas
        puntosDesdeLinea.size >= 2 -> puntosDesdeLinea
        puntosDesdeParadas.size >= 2 -> puntosDesdeParadas
        else -> emptyList()
    }
    Log.d("MAPA", "DrawLineaOnMap -> puntosDesdeExternas=${puntosDesdeExternas.size} puntosDesdeLinea=${puntosDesdeLinea.size} puntosDesdeParadas=${puntosDesdeParadas.size}")
    Log.d("MAPA", "DrawLineaOnMap -> puntosPolyline.size = ${puntosPolyline.size}")

    if (puntosPolyline.size >= 2) {
        Polyline(
            points = puntosPolyline,
            color = Color(android.graphics.Color.parseColor(colorRoute)),
            width = 8f,
            geodesic = true
        )
    }
}

/**
 * Función auxiliar para convertir color HEX a HUE de marcador
 */
private fun parseColorToHue(colorHex: String): Float {
    return when (colorHex.uppercase()) {
        "#FF0000", "#F44336" -> BitmapDescriptorFactory.HUE_RED
        "#00FF00", "#4CAF50" -> BitmapDescriptorFactory.HUE_GREEN
        "#0000FF", "#2196F3" -> BitmapDescriptorFactory.HUE_BLUE
        "#FFA500", "#FF9800" -> BitmapDescriptorFactory.HUE_ORANGE
        "#800080", "#9C27B0" -> BitmapDescriptorFactory.HUE_VIOLET
        "#FFC0CB", "#E91E63" -> BitmapDescriptorFactory.HUE_ROSE
        "#FFFF00", "#FFEB3B" -> BitmapDescriptorFactory.HUE_YELLOW
        "#00FFFF", "#00BCD4" -> BitmapDescriptorFactory.HUE_CYAN
        else -> BitmapDescriptorFactory.HUE_AZURE // Por defecto
    }
}
@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, rutaId: Int? = null, lineaId: Int? = null) {

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showSatellite by remember { mutableStateOf(false) }

    // 🧠 ViewModel

    val viewModel: MapaViewModel = viewModel()
    val lineasPorCoordenada by viewModel.lineasPorCoordenada.collectAsState()
    val paradas by viewModel.paradas.collectAsState()
    val nombreLinea by viewModel.nombreLinea.collectAsState()
    val coordenadas by viewModel.coordenadas.collectAsState()
    val ubicacionBuscada by viewModel.ubicacionBuscada.collectAsState()
    val paradasCercanas by viewModel.paradasCercanas.collectAsState()
    val lineasViewModel: LineasViewModel = viewModel()
    val lineaSeleccionada by lineasViewModel.lineaDetalle.collectAsState()
    val lineas by lineasViewModel.lineas.collectAsState()
    // Cargar la línea específica cuando cambie el lineaId
    LaunchedEffect(lineaId) {
        if (lineaId != null) {
            println("🟢 MapScreen - Iniciando, llamando cargarLineas")
            lineasViewModel.cargarLineaPorId(lineaId)
        }
    }
    // 👇 NUEVO ESTADO: Lista de sugerencias de autocompletado
    val sugerencias by remember(paradas, searchQuery.text) {
        if (searchQuery.text.length >= 3) { // Muestra sugerencias si hay al menos 3 caracteres
            val query = searchQuery.text.trim()
            mutableStateOf(
                paradas
                    .filter { it.nombreParada?.contains(query, ignoreCase = true) == true }
                    .take(5)
            )
        } else {
            mutableStateOf(emptyList())
        }
    }
    val sugerenciasAmostrar = if (paradasCercanas.isNotEmpty()) {
        paradasCercanas
    } else {
        sugerencias
    }

    // 🌍 Cliente de ubicación
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

    // 🌍 Posición inicial Cochabamba
    val cochabamba = LatLng(-17.3895, -66.1568)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cochabamba, 15f)
    }
    val lineaParaMostrar by viewModel.lineaParaMostrar.collectAsState()

// estado local para dibujar una sola vez dentro del composable del mapa
    var lineaTemporal by remember { mutableStateOf<com.example.appautomovil.data.models.Linea?>(null) }


    // 🚀 Cargar paradas cuando se selecciona una ruta
    LaunchedEffect(rutaId, lineaId) {
        //if (rutaId != null || lineaId != null) {
            viewModel.limpiarResultadosBusqueda()
        //}
        when {
            lineaId != null -> viewModel.cargarParadasPorLinea(lineaId)
            rutaId != null -> viewModel.cargarDatosPorRutaId(rutaId)
            else -> viewModel.cargarParadas()
        }
    }
    LaunchedEffect(ubicacionBuscada) {
        ubicacionBuscada?.let { latLng ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)
        }
    }
    LaunchedEffect(lineaParaMostrar) {
        lineaParaMostrar?.let { linea ->
            lineaTemporal = linea
            // una vez transferida a 'lineaTemporal' pedimos limpiar el evento en el VM
            viewModel.clearLineaParaMostrar()
        }
    }
    // ✅ Centrar el mapa automáticamente en la primera parada de la ruta seleccionada
    LaunchedEffect(paradas) {
        if ((rutaId != null || lineaId !=null) && paradas.isNotEmpty()) {
            paradas.firstOrNull()?.ubicacion?.toLatLngOrNull()?.let { latLng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    latLng, 17f
                )
            }
        }
    }


    // 🚀 Detectar ubicación actual
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                userLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    // Centrar automáticamente cuando lleguen lineasPorCoordenada
    LaunchedEffect(key1 = lineasPorCoordenada) {
        val puntos = lineasPorCoordenada
            .firstOrNull()
            ?.rutas?.flatMap { it.coordenadas ?: emptyList() }
            ?.mapNotNull { it.coordenada?.toLatLngOrNull() } ?: emptyList()

        if (puntos.isNotEmpty()) {
            // DEBUG: imprime los primeros 5 puntos y sus campos latitude/longitude
            puntos.take(5).forEachIndexed { i, p ->
                Log.d("MAPA", "puntos[$i] => latitude=${p.latitude}, longitude=${p.longitude}")
            }

            // calcular centro promedio (asegúrate que latitude / longitude estén correctos)
            val avgLat = puntos.map { it.latitude }.average()
            val avgLng = puntos.map { it.longitude }.average()
            val centro = LatLng(avgLat, avgLng)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(centro, 15f)
            Log.d("MAPA", "Centrando cámara en centro promedio de la línea: $centro, puntos=${puntos.size}")
        }
    }



    // El contenedor principal es un Box para superponer elementos (Mapa, Barra, FABs)
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

            // 📍 Marcador de ubicación actual
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Tu ubicación actual",
                    snippet = "Estás aquí"
                )
            }
            ubicacionBuscada?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Ubicación buscada",
                    snippet = "Centro de búsqueda (radio de 2km)",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) // Color rojo
                )
            }

            paradasCercanas.forEach { parada ->
                parada.ubicacion?.toLatLngOrNull()?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = parada.nombreParada ?: "Parada Cercana",
                        snippet = "Tipo: ${parada.tipoParada}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) // Color verde para las paradas cercanas
                    )
                }
            }
            // Dibuja las líneas encontradas por la búsqueda de la coordenada
            lineasPorCoordenada.forEachIndexed { idx, encontrada ->
                // Puedes elegir color por índice para distinguir (opcional)
                val routeColor = when (idx % 4) {
                    0 -> "#2196F3" // azul
                    1 -> "#FF9800" // naranja
                    2 -> "#4CAF50" // verde
                    else -> "#9C27B0" // violeta
                }

                // Llama a tu función existente — recibe Linea como antes
                DrawLineaOnMap(
                    linea = encontrada,
                    colorMarker = routeColor,
                    colorRoute = routeColor,
                    // si quieres priorizar coordenadas que trae el MapaViewModel puedes pasarlas:
                    coordenadasExternas = encontrada.rutas?.flatMap { it.coordenadas ?: emptyList() }
                )
            }

            lineaSeleccionada?.let { linea ->
                DrawLineaOnMap(
                    linea = linea,
                    colorMarker = "#2196F3",
                    colorRoute = "#2196F3",
                    coordenadasExternas = coordenadas // <-- usa la lista que ya cargas en MapaViewModel
                )
            }
            lineaTemporal?.let { linea ->
                DrawLineaOnMap(
                    linea = linea,
                    colorMarker = "#2196F3",
                    colorRoute = "#2196F3",
                    coordenadasExternas = linea.rutas?.flatMap { it.coordenadas ?: emptyList() }
                )
                // si quieres que desaparezca inmediatamente después de dibujarse una sola vez,
                // puedes limpiar lineaTemporal aquí (pero si lo haces, no se verá).
                // normalmente la mantienes hasta que otra acción la reemplace.
            }

        }

        // 🔝 Barra superior y Sugerencias
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .zIndex(2f) // Asegura que esté por encima del mapa
        ) {
            // Contenedor de la barra de búsqueda (para el fondo blanco redondeado)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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

                    IconButton(onClick = {
                        lineasViewModel.limpiarLineaSeleccionada()
                        // Aquí puedes añadir la lógica de Geocodificación (búsqueda de calles)
                        viewModel.buscarDireccion(context, searchQuery.text)
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar ubicación")
                    }
                }
            }

            // 👇 SECCIÓN DE SUGERENCIAS
            if (sugerencias.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp) // Limitar altura de la lista
                    ) {
                        items(sugerenciasAmostrar) { selectedParada ->
                            SuggestionItem(parada = selectedParada) {
                                // Acción al seleccionar una sugerencia
                                 searchQuery = TextFieldValue(selectedParada.nombreParada ?: "")

                                // Cerrar sugerencias al seleccionar y centrar el mapa
                                selectedParada.ubicacion?.toLatLngOrNull()?.let { latLng ->
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                                }
                            }
                        }
                    }
                }
            }
        }


        // ⚙️ Controles de vista, zoom y ubicación
        // 🚨 IMPORTANTE: Este Column es hijo directo del Box principal, por eso usa .align()
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd) // <- ESTO SOLUCIONA EL ERROR DE ALIGN
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

// 👇 COSAS QUE DEBEN IR FUERA DE LA FUNCIÓN MAPSCREEN
// 👇 COSAS QUE DEBEN IR FUERA DE LA FUNCIÓN MAPSCREEN

@Composable
fun SuggestionItem(parada: Parada, onSuggestionClick: (Parada) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSuggestionClick(parada) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Parada sugerida",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = parada.nombreParada ?: "Parada Desconocida",
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "Tipo: ${parada.tipoParada ?: "N/A"}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
    Divider()
}