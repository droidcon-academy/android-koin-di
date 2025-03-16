package com.droidcon.weatherscope.injection

import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherViewModel
import com.droidcon.weatherscope.ui.screens.forecast.ForecastViewModel
import com.droidcon.weatherscope.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CurrentWeatherViewModel(get(), get(), get()) }
    viewModel { ForecastViewModel() }
    viewModel { SettingsViewModel(appPreferences = get()) }
}