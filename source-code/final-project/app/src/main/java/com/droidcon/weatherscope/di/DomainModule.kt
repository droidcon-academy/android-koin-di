package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.data.repositories.WeatherRepository
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCaseImpl
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
object DomainModule {
    @Factory
    fun provideCurrentWeatherUseCase(repo: WeatherRepository) : GetCurrentWeatherUseCase = GetCurrentWeatherUseCaseImpl(repo)

    @Single
    fun provideWeatherDomain(useCase: GetCurrentWeatherUseCase) = WeatherDomain(useCase)

    @Factory
    fun provideForecastUseCase(repo: WeatherRepository) = GetWeatherForecastUseCase(repo)
}