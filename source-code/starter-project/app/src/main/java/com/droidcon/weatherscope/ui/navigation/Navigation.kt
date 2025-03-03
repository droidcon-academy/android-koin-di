package com.droidcon.weatherscope.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherScreen
import com.droidcon.weatherscope.ui.screens.forecast.ForecastScreen
import com.droidcon.weatherscope.ui.screens.settings.SettingsScreen
import com.droidcon.weatherscope.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(Routes.CURRENT_WEATHER) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(Routes.CURRENT_WEATHER) {
            CurrentWeatherScreen(
                onNavigateToForecast = { navController.navigate(Routes.FORECAST) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.FORECAST) { ForecastScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}