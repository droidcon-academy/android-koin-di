package com.droidcon.weatherscope.injection

import com.droidcon.weatherscope.data.repositories.WeatherRepository
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherRepository(get()) }
}