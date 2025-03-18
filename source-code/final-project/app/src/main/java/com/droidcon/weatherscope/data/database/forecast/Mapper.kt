package com.droidcon.weatherscope.data.database.forecast

import com.droidcon.weatherscope.domain.models.WeatherForecastItem
import java.net.URL
import java.time.LocalDate

// Entity -> Domain
fun WeatherForecastEntity.toDomain(): WeatherForecastItem {
    return WeatherForecastItem(
        date = LocalDate.parse(date), // assumes ISO-8601 stored string
        weatherDescription = weatherDescription,
        tempMin = tempMin,
        tempMax = tempMax,
        iconLink = URL(iconLink)
    )
}

// Domain -> Entity
fun WeatherForecastItem.toEntity(): WeatherForecastEntity {
    return WeatherForecastEntity(
        date = date.toString(),
        weatherDescription = weatherDescription,
        tempMin = tempMin,
        tempMax = tempMax,
        iconLink = iconLink.toString(),
        fetchedAt = System.currentTimeMillis()
    )
}