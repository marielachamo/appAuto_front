package com.example.appautomovil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appautomovil.ui.screens.HomeScreen
import com.example.appautomovil.ui.screens.MapScreen
import com.example.appautomovil.ui.screens.RouteListScreen
import com.example.appautomovil.ui.theme.AppAutomovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Verificamos y pedimos permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }

        // ✅ Mostramos la interfaz de la aplicación
        setContent {
            AppAutomovilTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // 🏠 Pantalla principal
        composable("home") {
            HomeScreen(onNavigateToMap = { navController.navigate("map") })
        }

        // 🗺️ Pantalla de mapa sin ruta seleccionada
        composable("map") {
            MapScreen(navController)
        }

        // 🚌 Lista de rutas
        composable("routeList") {
            RouteListScreen(navController)
        }

        // 📍 Pantalla de mapa con una ruta específica (ejemplo: /mapScreen/5)
        composable(
            route = "mapScreen/{rutaId}",
            arguments = listOf(navArgument("rutaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutaId = backStackEntry.arguments?.getInt("rutaId")
            MapScreen(navController = navController, rutaId = rutaId)
        }
    }
}
