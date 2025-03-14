package com.droidcon.weatherscope.ui.screens.currentweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.ui.common.TextFieldState
import com.droidcon.weatherscope.utils.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
    private val weatherDomain: WeatherDomain,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _dataState = MutableStateFlow<DataState<CurrentWeatherState>>(DataState.Loading)
    val dataState: StateFlow<DataState<CurrentWeatherState>> = _dataState

    init {
        loadWeather()
    }

    private fun loadWeather() {
        _dataState.value = DataState.Loading
        viewModelScope.launch {
            try {
                appPreferences.currentCityName
                    .flatMapLatest { cityName ->
                        weatherDomain.getCurrentWeather(city = cityName)
                    }
                    .catch { exception ->
                        _dataState.value = DataState.Error(exception.message ?: "Unknown error")
                    }
                    .collect { weatherData ->
                        _dataState.value = when (weatherData) {
                            is DataState.Error -> DataState.Success(
                                CurrentWeatherState("Error loading weather data: ${weatherData.message}")
                            )
                            DataState.Loading -> DataState.Loading
                            is DataState.Success -> DataState.Success(
                                (CurrentWeatherState("weather: ${weatherData.state.description}"))
                            )
                        }
                    }
            } catch (e: Exception) {
                _dataState.value = DataState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setCurrentLocation() {
        val currentState = _dataState.value
        viewModelScope.launch {
            try {
                if (currentState is DataState.Success) {
                    if (currentState.state.cityTextFieldState.value.isBlank()) {
                        _dataState.value = DataState.Success(
                            currentState.state.copy(
                                cityTextFieldState = TextFieldState(
                                    value = currentState.state.cityTextFieldState.value,
                                    errorMessage = "city name cannot be blank",
                                    isError = true
                                )
                            ))
                    } else {
                        appPreferences.setCurrentCityName(currentState.state.cityTextFieldState.value)
                        loadWeather()
                    }
                }
            } catch (e: Exception) {
                if (currentState is DataState.Success) {

                    _dataState.value = DataState.Success(
                        currentState.state.copy(
                            cityTextFieldState = TextFieldState(
                                value = currentState.state.cityTextFieldState.value,
                                errorMessage = "Failed to update current city: ${e.message}"
                            )
                        )
                    )
                }
            }
        }
    }

    fun onLocationTextValueChanged(newValue: String) {
        val currentState = _dataState.value
        if (currentState is DataState.Success) {

            _dataState.value = DataState.Success(
                currentState.state.copy(
                    cityTextFieldState = TextFieldState(
                        value = newValue
                    )
                )
            )
        }
    }
}