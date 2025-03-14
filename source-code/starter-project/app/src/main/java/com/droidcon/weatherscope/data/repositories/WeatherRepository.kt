package com.droidcon.weatherscope.data.repositories

import com.droidcon.weatherscope.data.network.services.openweather.WeatherApiService
import com.droidcon.weatherscope.data.network.services.openweather.models.CurrentWeatherResponse
import com.droidcon.weatherscope.data.network.utils.ApiCallResult
import com.droidcon.weatherscope.data.network.utils.safeApiCall
import kotlinx.coroutines.flow.Flow

class WeatherRepository(private val apiService: WeatherApiService) {

    fun getCurrentWeatherByCity(city: String): Flow<ApiCallResult<CurrentWeatherResponse>> =
        safeApiCall {
            apiService.getCurrentWeatherByCity(city)
        }

    fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): Flow<ApiCallResult<CurrentWeatherResponse>> =
        safeApiCall {
            apiService.getCurrentWeatherByCoordinates(lat, lon)
        }
}