package com.droidcon.weatherscope.ui.navigation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.droidcon.weatherscope.R
import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherScreen
import com.droidcon.weatherscope.ui.screens.forecast.ForecastScreen
import com.droidcon.weatherscope.ui.screens.settings.SettingsScreen
import com.droidcon.weatherscope.ui.screens.splash.SplashScreen

@SuppressLint("RestrictedApi")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStack.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Forecast
    )
    val navTitle = bottomNavItems.firstOrNull { it.route == currentDestination?.route }?.title

    Scaffold(
        topBar = {
            provideTopAppBar(currentRoute = currentDestination?.route, navTitle = navTitle, navController = navController)
        },
        bottomBar = {
            if (currentDestination?.route == Routes.CURRENT_WEATHER || currentDestination?.route == Routes.FORECAST ) NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = stringResource(item.navText),
                                modifier = item.size?.let { Modifier.size(it.dp) } ?: Modifier
                            )
                        },
                        label = { Text(stringResource(item.navText)) },
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            val previousDestination = backStack.firstOrNull { it.destination.route == item.route }
                            previousDestination?.let { navController.popBackStack(item.route, false) } ?: run {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Routes.SPLASH, Modifier.padding(innerPadding)) {
            composable(Routes.SPLASH) {
                SplashScreen(onTimeout = {
                    navController.navigate(Routes.CURRENT_WEATHER) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.FORECAST) {
                ForecastScreen()
            }
            composable(Routes.CURRENT_WEATHER) {
                CurrentWeatherScreen(onNavigateToSettings = { paramValue ->
                    navController.navigate(Routes.createSettingsRoute(paramValue))
                }
                )
            }
            composable(
                route = Routes.SETTINGS_WITH_PARAM,
                arguments = listOf(
                    navArgument("paramName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val paramValue = backStackEntry.arguments?.getString("paramName") ?: ""
                SettingsScreen(selectedCity = paramValue)
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    @StringRes val title: Int,
    @StringRes val navText: Int,
    val icon: ImageVector,
    val size: Int? = null
) {
    data object Home :
        BottomNavItem(Routes.CURRENT_WEATHER, title = R.string.title_home, navText = R.string.home, Icons.Filled.Home)
    data object Forecast : BottomNavItem(Routes.FORECAST, title = R.string.title_forecast, navText = R.string.forecast, Icons.Default.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun provideTopAppBar(currentRoute: String?, navTitle: Int?, navController: NavController): Unit {
    when {
        currentRoute?.contains(Routes.SETTINGS) == true ->
            TopAppBar(
                title = {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.width(24.dp)
                        )
                    }
                }
            )
        currentRoute != Routes.SPLASH ->  TopAppBar(
            title = { navTitle?.let{ Text(stringResource(id = navTitle)) } ?: Unit },
        )
    }
}