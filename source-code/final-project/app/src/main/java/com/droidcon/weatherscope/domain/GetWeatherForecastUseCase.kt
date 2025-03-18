package com.droidcon.weatherscope.domain

import com.droidcon.weatherscope.common.capitalize
import com.droidcon.weatherscope.data.network.services.openweather.models.WeatherForecastResponse
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import com.droidcon.weatherscope.domain.models.WeatherForecastItem
import com.droidcon.weatherscope.ui.common.DataState
import kotlinx.coroutines.flow.Flow
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetWeatherForecastUseCase (private val weatherRepository: WeatherRepository) {

    suspend fun getWeatherForecast(latitude: Double, longitude: Double): Flow<DataState<List<WeatherForecastItem>>> {

        return weatherRepository.getWeatherForecast(
            latitude = latitude,
            longitude = longitude
        )
    }
}

/**
 * Extension function to convert ForecastItem from the API response to domain model WeatherForecast
 *
 * @return WeatherForecast domain model with essential forecast information
 */
fun WeatherForecastResponse.ForecastItem.toDomain(): WeatherForecastItem {
    // Extract the primary weather condition (first in the list)
    val primaryWeatherCondition = weatherConditions.firstOrNull() ?: WeatherForecastResponse.WeatherCondition(
        0,
        "Unknown",
        "No weather data available",
        ""
    )

    // Create icon URL
    val iconUrl = if (primaryWeatherCondition.icon.isNotEmpty()) {
        URL("https://openweathermap.org/img/wn/${primaryWeatherCondition.icon}@2x.png")
    } else {
        URL("https://openweathermap.org/img/wn/01d@2x.png") // Default icon
    }

    // Parse the date from the date_txt field
    val date = LocalDate.parse(
        dateTimeText.split(" ")[0],
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    )

    return WeatherForecastItem(
        date = date,
        weatherDescription = primaryWeatherCondition.description.capitalize(),
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        iconLink = iconUrl
    )
}

/**
 * Extension function to convert ForecastResponse to a list of WeatherForecast domain models
 *
 * @return List of WeatherForecast domain models
 */
fun WeatherForecastResponse.toDomain(): List<WeatherForecastItem> {
    // Group forecast items by date to get daily forecasts
    val groupedByDate = forecastItems.groupBy {
        it.dateTimeText.split(" ")[0] // Group by date part of the datetime string
    }

    // For each date, select one forecast item (noon if available, or first item)
    return groupedByDate.map { (_, items) ->
        // Try to find forecast for noon (12:00:00) or use the first item
        val noonForecast = items.find { it.dateTimeText.contains("12:00:00") } ?: items.first()
        noonForecast.toDomain()
    }.sortedBy { it.date } // Sort by date
}