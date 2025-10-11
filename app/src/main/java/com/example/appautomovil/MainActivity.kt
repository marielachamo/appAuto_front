package com.example.appautomovil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appautomovil.ui.screens.HomeScreen
import com.example.appautomovil.ui.screens.MapScreen
import com.example.appautomovil.ui.theme.AppAutomovilTheme
import org.osmdroid.config.Configuration
import com.example.appautomovil.ui.screens.RouteListScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Inicializamos la configuración de osmdroid
        Configuration.getInstance().userAgentValue = packageName

        // Verificamos y pedimos permisos de ubicación si no están concedidos
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

        // Renderizamos la interfaz con Compose
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
        composable("home") {
            HomeScreen(onNavigateToMap = { navController.navigate("map") })
        }
        composable("map") {
            MapScreen(navController)
        }
        composable("routeList") {
            RouteListScreen(navController)
        }
    }

}
