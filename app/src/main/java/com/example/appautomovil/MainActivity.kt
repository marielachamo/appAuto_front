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
import com.example.appautomovil.ui.screens.MapScreen
import com.example.appautomovil.ui.screens.RouteListScreen
import com.example.appautomovil.ui.theme.AppAutomovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Verificamos permisos de ubicación
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

        // ✅ Cargamos la app
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

    NavHost(navController = navController, startDestination = "mapScreen") {

        // 🗺️ Pantalla de mapa general (inicio de la app)
        composable("mapScreen") {
            MapScreen(navController)
        }

        // 🚌 Lista de rutas
        composable("routeList") {
            RouteListScreen(navController)
        }

        // 🚏 Pantalla del mapa con una ruta específica
        composable(
            route = "mapScreen/{rutaId}",
            arguments = listOf(navArgument("rutaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutaId = backStackEntry.arguments?.getInt("rutaId")
            MapScreen(navController = navController, rutaId = rutaId)
        }

        // 🚍 Pantalla del mapa para una línea específica
        composable(
            route = "mapScreenLinea/{lineaId}",
            arguments = listOf(navArgument("lineaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lineaId = backStackEntry.arguments?.getInt("lineaId")
            MapScreen(navController = navController, rutaId = null, lineaId = lineaId)
        }

        // 🕓 Nueva pantalla: Detalle de la línea (horarios e info)
        composable(
            route = "linea/{idLinea}/detalle",
            arguments = listOf(navArgument("idLinea") { type = NavType.IntType })
        ) { backStackEntry ->
            val idLinea = backStackEntry.arguments?.getInt("idLinea") ?: 0
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.appautomovil.ui.viewmodel.LineasViewModel>()
            com.example.appautomovil.ui.screens.LineaDetalleScreen(
                idLinea = idLinea,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

