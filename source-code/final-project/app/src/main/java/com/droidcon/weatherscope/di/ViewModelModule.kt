package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.StringResourcesProvider
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherViewModel
import com.droidcon.weatherscope.ui.screens.forecast.ForecastViewModel
import com.droidcon.weatherscope.ui.screens.settings.SettingsViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import org.koin.core.qualifier.named
import org.koin.dsl.module

@Module
object ViewModelModule {
    @KoinViewModel
    fun provideCurrentWeatherViewModel(
        domain: WeatherDomain,
        prefs: AppPreferences,
        locationUseCase: GetCurrentLocationUseCase,
        resources: StringResourcesProvider
    ) = CurrentWeatherViewModel(domain, prefs, locationUseCase, resources)

    @KoinViewModel
    fun provideSettingsViewModel(
        prefs: AppPreferences,
        resources: StringResourcesProvider,
        @InjectedParam selectedCity: String
    ) = SettingsViewModel(prefs, resources, selectedCity)

    @Scope(name = "ForecastScope")
    @Scoped
    fun provideForecastViewModel(
        prefs: AppPreferences,
        usecase: GetWeatherForecastUseCase,
        resources: StringResourcesProvider
    ) = ForecastViewModel(prefs, usecase, resources)
}