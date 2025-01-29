package com.droidcon.weatherscope.ui.screens.currentweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.ui.common.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
//    private val WeatherDomain: WeatherDomain
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<CurrentWeatherState>>(ScreenState.Loading)
    val screenState: StateFlow<ScreenState<CurrentWeatherState>> = _screenState

    init {
        /*loadWeather(location)*/

        _screenState.value = ScreenState.Success(CurrentWeatherState("Current weather, viewmodel data."))
    }

    fun loadWeather(location: String) {
        viewModelScope.launch {
            try {
                _screenState.value = ScreenState.Loading

                // Fetch data from API
                // TODO: domain.fetchCurrentWeather(location)

                // Observe from local db
                /* TODO: repository.getCachedWeather(location).collect { weatherEntity ->
                    _screenState.value = ScreenState.Success(weatherEntity)
                }*/
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }
}