package com.droidcon.weatherscope.ui.screens.currentweather

import com.droidcon.weatherscope.ui.common.TextFieldState

data class CurrentWeatherState(
    val text: String,
    val cityTextFieldState: TextFieldState = TextFieldState()
)
