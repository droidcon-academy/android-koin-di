package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherViewModel
import com.droidcon.weatherscope.ui.screens.forecast.ForecastViewModel
import com.droidcon.weatherscope.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CurrentWeatherViewModel(weatherDomain = get(), appPreferences = get(), getCurrentLocationUseCase = get(), stringResourcesProvider = get()) }
    viewModel { ForecastViewModel(appPreferences = get(), getWeatherForecastUseCase = get(), stringResourcesProvider = get()) }
    viewModel { SettingsViewModel(appPreferences = get(), stringResourcesProvider = get()) }
}