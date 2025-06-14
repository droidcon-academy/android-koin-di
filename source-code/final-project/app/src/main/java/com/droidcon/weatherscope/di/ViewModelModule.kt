package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherViewModel
import com.droidcon.weatherscope.ui.screens.forecast.ForecastViewModel
import com.droidcon.weatherscope.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CurrentWeatherViewModel(weatherDomain = get(), appPreferences = get(), getCurrentLocationUseCase = get(), stringResourcesProvider = get()) }
    viewModel { params ->
        SettingsViewModel(appPreferences = get(), stringResourcesProvider = get(), selectedCity = params.get<String>()) }
    scope(named("ForecastScope")) {
        scoped { ForecastViewModel(get(), get(), get()) }
    }
}