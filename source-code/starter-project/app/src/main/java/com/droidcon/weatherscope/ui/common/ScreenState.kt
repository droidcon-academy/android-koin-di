package com.droidcon.weatherscope.ui.common

import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherState

sealed class ScreenState<out T> {
    data object Loading : ScreenState<Nothing>()
    data class Success<T>(val state: T) : ScreenState<T>()
    data class Error(val message: String) : ScreenState<Nothing>()
}
