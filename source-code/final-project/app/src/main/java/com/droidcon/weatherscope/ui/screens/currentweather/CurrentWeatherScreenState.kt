package com.droidcon.weatherscope.ui.screens.currentweather

import com.droidcon.weatherscope.common.TemperatureUnit
import com.droidcon.weatherscope.ui.common.TextFieldState
import java.net.URL

data class CurrentWeatherScreenState(
    val cityTextFieldState: TextFieldState = TextFieldState(),
    val latTextFieldState: TextFieldState = TextFieldState(),
    val lonTextFieldState: TextFieldState = TextFieldState(),
    val data: CurrentWeatherUiState? = null
)

data class CurrentWeatherUiState(
    val locationName: String,
    val status: String,
    val description: String,
    val temperature: String, // Formatted with unit
    val humidity: String,    // Formatted with %
    val iconLink: URL,
    val temperatureUnit: TemperatureUnit
)
