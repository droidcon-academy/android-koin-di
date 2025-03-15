package com.droidcon.weatherscope.domain

import com.droidcon.weatherscope.domain.models.CurrentWeather
import com.droidcon.weatherscope.ui.common.DataState
import kotlinx.coroutines.flow.Flow

class WeatherDomain(private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase) {

    suspend fun getCurrentWeather(city: String): Flow<DataState<CurrentWeather>> {
        return getCurrentWeatherUseCase.getCurrentWeather(cityName = city)
    }

    suspend fun getCurrentWeatherByCoordinates(latitude: Double, longitude: Double): Flow<DataState<CurrentWeather>> {
        return getCurrentWeatherUseCase.getCurrentWeather(latitude = latitude, longitude = longitude)
    }
}