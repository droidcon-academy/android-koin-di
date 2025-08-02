package com.droidcon.weatherscope.domain

import com.droidcon.weatherscope.data.network.services.openweather.models.CurrentWeatherResponse
import com.droidcon.weatherscope.data.network.services.openweather.models.WeatherCondition
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import com.droidcon.weatherscope.domain.models.CurrentWeather
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.common.capitalize
import kotlinx.coroutines.flow.Flow
import java.net.URL

class GetCurrentWeatherUseCaseImpl(private val currentWeatherRepository: WeatherRepository): GetCurrentWeatherUseCase {

    override fun getCurrentWeather(cityName: String): Flow<DataState<CurrentWeather>> {
        val response = currentWeatherRepository.getCurrentWeatherByCity(
            city = cityName
        )

        return response.toDomainStateFlow { apiResponse ->
            apiResponse.toDomain()
        }
    }

    override fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Flow<DataState<CurrentWeather>> {
        val response = currentWeatherRepository.getCurrentWeatherByCoordinates(
            lat = latitude,
            lon = longitude
        )

        return response.toDomainStateFlow { apiResponse ->
            apiResponse.toDomain()
        }
    }
}

/**
 * Extension function to convert CurrentWeatherResponse to domain model CurrentWeather
 *
 * @return CurrentWeather domain model with essential weather information
 */
fun CurrentWeatherResponse.toDomain(): CurrentWeather {
    // Extract the primary weather condition (first in the list)
    val primaryWeatherCondition = weatherConditions.firstOrNull() ?: WeatherCondition(
        0,
        "Unknown",
        "No weather data available",
        ""
    )

    val iconUrl = if (primaryWeatherCondition.icon.isNotEmpty()) {
        URL("https://openweathermap.org/img/wn/${primaryWeatherCondition.icon}@2x.png")
    } else {
        URL("https://openweathermap.org/img/wn/01d@2x.png") // Default icon
    }

    return CurrentWeather(
        locationName = name,
        lat = coordinates.latitude,
        lon = coordinates.longitude,
        status = primaryWeatherCondition.main,
        description = primaryWeatherCondition.description.capitalize(),
        temp = main.temperature,
        humidity = main.humidity.toDouble(),
        iconLink = iconUrl
    )
}

interface GetCurrentWeatherUseCase {
    fun getCurrentWeather(cityName: String): Flow<DataState<CurrentWeather>>
    fun getCurrentWeather(latitude: Double, longitude: Double): Flow<DataState<CurrentWeather>>
}