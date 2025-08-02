package com.droidcon.weatherscope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.droidcon.weatherscope.ui.navigation.AppNavigation
import com.droidcon.weatherscope.ui.theme.WeatherScopeTheme
import com.droidcon.weatherscope.common.AppPreferences
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.core.content.ContextCompat
import com.droidcon.weatherscope.common.AndroidPermissionChecker
import com.droidcon.weatherscope.common.AppStringResourcesProvider
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.GetCurrentLocationUseCaseImpl
import com.droidcon.weatherscope.common.StringResourcesProvider
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCaseImpl
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherViewModel
import com.droidcon.weatherscope.ui.screens.forecast.ForecastViewModel
import com.droidcon.weatherscope.ui.screens.settings.SettingsViewModel

val LocalCurrentWeatherViewModel = compositionLocalOf<CurrentWeatherViewModel> { error("No CurrentWeatherViewModel provided") }
val LocalForecastViewModel = compositionLocalOf<ForecastViewModel> { error("No ForecastViewModel provided") }
val LocalSettingsViewModel = compositionLocalOf<SettingsViewModel> { error("No SettingsViewModel provided") }


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalCurrentWeatherViewModel provides createCurrentWeatherViewModel(),
                LocalForecastViewModel provides createForecastViewModel(),
                LocalSettingsViewModel provides createSettingsViewModel()
            ){
            val isDarkTheme by appPreferences.isDarkTheme.collectAsState(initial = false)

            WeatherScopeTheme(darkThemeEnabled = isDarkTheme) {
                AppNavigation()
            }
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
}

// Extension properties for MainActivity
val ComponentActivity.appPreferences: AppPreferences
    get() = (applicationContext as MainApplication).appPreferences

val ComponentActivity.stringResourcesProvider: StringResourcesProvider
    get() = AppStringResourcesProvider(this.resources)

val ComponentActivity.getCurrentLocationUseCase: GetCurrentLocationUseCase
    get() = GetCurrentLocationUseCaseImpl(
        applicationContext,
        AndroidPermissionChecker(applicationContext)
    )

// Repository - access from Application
val ComponentActivity.weatherRepository: WeatherRepository
    get() = (applicationContext as MainApplication).weatherRepository

// Extension function to create ViewModel
fun ComponentActivity.createCurrentWeatherViewModel(): CurrentWeatherViewModel {
    return CurrentWeatherViewModel(
        weatherDomain = WeatherDomain(GetCurrentWeatherUseCaseImpl(weatherRepository)),
        appPreferences = this.appPreferences,
        getCurrentLocationUseCase = this.getCurrentLocationUseCase,
        stringResourcesProvider = this.stringResourcesProvider
    )
}

// Extension function to create ForecastViewModel
fun ComponentActivity.createForecastViewModel(): ForecastViewModel {
    return ForecastViewModel(
        appPreferences = this.appPreferences,
        getWeatherForecastUseCase = GetWeatherForecastUseCase(weatherRepository),
        stringResourcesProvider = this.stringResourcesProvider
    )
}

// Extension function to create SettingsViewModel
fun ComponentActivity.createSettingsViewModel(): SettingsViewModel {
    return SettingsViewModel(
        appPreferences = this.appPreferences,
        stringResourcesProvider = this.stringResourcesProvider
    )
}