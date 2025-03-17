package com.droidcon.weatherscope.injection

import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import org.koin.dsl.module

val domainModule = module {
    factory { GetCurrentWeatherUseCase(get()) }
    single{ WeatherDomain(get()) }
    factory { GetWeatherForecastUseCase(get()) }
}