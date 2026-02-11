package com.nyinnovations.androidcleanarchitecturesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nyinnovations.androidcleanarchitecturesample.presentation.forecast.ForecastScreen
import com.nyinnovations.androidcleanarchitecturesample.presentation.forecast.ForecastViewModel
import com.nyinnovations.androidcleanarchitecturesample.presentation.weather.WeatherScreen
import com.nyinnovations.androidcleanarchitecturesample.presentation.weather.WeatherViewModel
import com.nyinnovations.androidcleanarchitecturesample.ui.theme.AndroidCleanArchitectureSampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCleanArchitectureSampleTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            val viewModel: WeatherViewModel = hiltViewModel()
            WeatherScreen(
                viewModel = viewModel,
                onCityClick = { city -> navController.navigate("forecast/$city") }
            )
        }
        composable(
            route = "forecast/{city}",
            arguments = listOf(navArgument("city") { type = NavType.StringType })
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city") ?: return@composable
            val viewModel: ForecastViewModel = hiltViewModel()
            ForecastScreen(
                cityName = city,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
