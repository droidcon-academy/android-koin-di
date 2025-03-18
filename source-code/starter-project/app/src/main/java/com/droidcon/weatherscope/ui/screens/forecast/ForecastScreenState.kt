package com.droidcon.weatherscope.ui.screens.forecast

import com.droidcon.weatherscope.common.TemperatureUnit
import java.net.URL

data class ForecastScreenState(
    val forecasts: List<WeatherForecastItemUiState> = emptyList(),
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)

data class WeatherForecastItemUiState(
    val date: String,
    val weatherDescription: String,
    val tempMin: String,
    val tempMax: String,
    val iconLink: URL,
    val temperatureUnit: TemperatureUnit
)
