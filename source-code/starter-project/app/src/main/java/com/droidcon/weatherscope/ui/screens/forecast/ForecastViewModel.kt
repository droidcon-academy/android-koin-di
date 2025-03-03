package com.droidcon.weatherscope.ui.screens.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.ui.common.ScreenState
import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForecastViewModel (
//    private val weatherForecastDomain: WeatherForecastDomain
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<ForecastState>>(ScreenState.Loading)
    val screenState: StateFlow<ScreenState<ForecastState>> = _screenState

    init {
        /*loadForecast()*/

        _screenState.value = ScreenState.Success(ForecastState("weather forecast, viewmodel data."))
    }

    fun loadForecast() {
        viewModelScope.launch {
            try {
                _screenState.value = ScreenState.Loading

                // Observe from local db - the forecasted weather.
                /* TODO: repository.getCachedWeather(location).collect { weatherEntity ->
                    _screenState.value = ScreenState.Success(weatherEntity)
                }*/
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }
}