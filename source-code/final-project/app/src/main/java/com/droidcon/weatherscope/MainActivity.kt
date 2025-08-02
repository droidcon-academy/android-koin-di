package com.droidcon.weatherscope

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.ui.navigation.AppNavigation
import com.droidcon.weatherscope.ui.theme.WeatherScopeTheme
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {

    private val appPreferences: AppPreferences by inject()

    val forecastScope = getKoin().createScope("FORECAST_SCOPE_ID", named("ForecastScope"))  // (1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Now appPreferences is already available (injected above)
            val isDarkTheme by appPreferences.isDarkTheme.collectAsState(initial = false)
            WeatherScopeTheme(darkThemeEnabled = isDarkTheme) {
                AppNavigation()  // Our NavHost with composable screens
            }
        }

        if (!hasLocationPermission()) {
            // Prompt the user to grant location permissions.
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close the custom Forecast scope to clean up resources when the Activity is destroyed.
        forecastScope.close()
    }
}