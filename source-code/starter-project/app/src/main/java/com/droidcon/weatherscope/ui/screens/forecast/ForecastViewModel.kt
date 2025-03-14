package com.droidcon.weatherscope.ui.screens.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.ui.common.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForecastViewModel (
//    private val weatherForecastDomain: WeatherForecastDomain
) : ViewModel() {

    private val _dataState = MutableStateFlow<DataState<ForecastState>>(DataState.Loading)
    val dataState: StateFlow<DataState<ForecastState>> = _dataState

    init {
        /*loadForecast()*/

        _dataState.value = DataState.Success(ForecastState("weather forecast, viewmodel data."))
    }

    fun loadForecast() {
        viewModelScope.launch {
            try {
                _dataState.value = DataState.Loading

                // Observe from local db - the forecasted weather.
                /* TODO: repository.getCachedWeather(location).collect { weatherEntity ->
                    _screenState.value = ScreenState.Success(weatherEntity)
                }*/
            } catch (e: Exception) {
                _dataState.value = DataState.Error(e.message ?: "Unknown error")
            }
        }
    }
}