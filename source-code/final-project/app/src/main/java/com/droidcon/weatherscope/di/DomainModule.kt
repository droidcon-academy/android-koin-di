package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCaseImpl
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import org.koin.dsl.module

val domainModule = module {
    factory<GetCurrentWeatherUseCase>{ GetCurrentWeatherUseCaseImpl(get()) }
    single { WeatherDomain(get()) }
    factory { GetWeatherForecastUseCase(get()) }
}