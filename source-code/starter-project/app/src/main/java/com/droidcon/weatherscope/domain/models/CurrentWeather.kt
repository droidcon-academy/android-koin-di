package com.droidcon.weatherscope.domain.models

import java.net.URL

data class CurrentWeather(
    val locationName: String,
    val status: String,
    val description: String,
    val temp: Double,
    val humidity: Double,
    val iconLink: URL
    )
