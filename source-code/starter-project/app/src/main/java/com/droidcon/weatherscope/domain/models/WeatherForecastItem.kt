package com.droidcon.weatherscope.domain.models

import java.net.URL
import java.time.LocalDate

data class WeatherForecastItem(
    val date: LocalDate,
    val weatherDescription: String,
    val tempMin: Double,
    val tempMax: Double,
    val iconLink: URL
)
