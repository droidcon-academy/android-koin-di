package com.droidcon.weatherscope.ui.screens.currentweather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.ui.common.TextFieldState
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.TemperatureUnit
import com.droidcon.weatherscope.common.toFahrenheit
import com.droidcon.weatherscope.domain.models.CurrentWeather
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CurrentWeatherViewModel(
    private val weatherDomain: WeatherDomain,
    private val appPreferences: AppPreferences,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _dataState =
        MutableStateFlow<DataState<CurrentWeatherScreenState>>(DataState.Loading)
    val dataState: StateFlow<DataState<CurrentWeatherScreenState>> = _dataState

    init {
        loadWeather()
    }

    private fun CurrentWeather.toUiState(temperatureUnit: TemperatureUnit): CurrentWeatherUiState {
        val formattedTemp = when (temperatureUnit) {
            TemperatureUnit.CELSIUS -> "${temp.roundToInt()}°C"
            TemperatureUnit.FAHRENHEIT -> "${temp.toFahrenheit().roundToInt()}°F"
        }

        return CurrentWeatherUiState(
            locationName = locationName,
            status = status,
            description = description,
            temperature = formattedTemp,
            humidity = "${humidity.roundToInt()}%",
            iconLink = iconLink,
            temperatureUnit = temperatureUnit
        )
    }

    // Helper function to apply temperature unit conversion
    private fun applyTemperatureUnit(
        weather: CurrentWeather,
        unit: TemperatureUnit
    ): CurrentWeather {
        return when (unit) {
            TemperatureUnit.CELSIUS -> weather // Already in Celsius, no conversion needed
            TemperatureUnit.FAHRENHEIT -> {
                weather.copy(
                    temp = weather.temp.toFahrenheit()
                )
            }
        }
    }

    private fun loadWeather() {
        _dataState.value = DataState.Loading

        viewModelScope.launch {
            try {
                val coordinatesFlow = combine(
                    appPreferences.currentCityLat.distinctUntilChanged(),
                    appPreferences.currentCityLon.distinctUntilChanged()
                ) { lat, lon -> Pair(lat, lon) }

                // For every new coordinate pair, fetch weather from the domain.
                val weatherFlow =
                    coordinatesFlow.distinctUntilChanged().flatMapLatest { (lat, lon) ->
                        weatherDomain.getCurrentWeatherByCoordinates(
                            latitude = lat,
                            longitude = lon
                        )
                    }

                combine(
                    weatherFlow,
                    appPreferences.temperatureUnit.distinctUntilChanged()
                ) { weatherData, tempUnit ->
                    Pair(weatherData, tempUnit)
                }
                    .catch { exception ->
                        _dataState.value = DataState.Error(exception.message ?: "Unknown error")
                    }
                    .collect { (weatherData, tempUnit) ->
                        _dataState.value = when (weatherData) {
                            is DataState.Error -> DataState.Error(
                                "Error loading weather data: ${weatherData.message}"
                            )

                            DataState.Loading -> DataState.Loading
                            is DataState.Success -> {
                                val convertedWeather =
                                    applyTemperatureUnit(weatherData.state, tempUnit)
                                DataState.Success(
                                    state = CurrentWeatherScreenState(
                                        data = convertedWeather.toUiState(tempUnit)
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _dataState.value = DataState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun setLocationCoordinates(lat: Double, lon: Double) {
        try {
            appPreferences.setCurrentCityLat(lat)
            appPreferences.setCurrentCityLon(lon)
        } catch (e: Exception) {
            _dataState.value =
                DataState.Error("Error saving lat lon: ${(e.message ?: "Unknown error")}")
        }
    } // loadWeather() is listening for changes in appPreferences data since init()

    fun setCurrentCityNameLocation() {
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
                            )
                        )
                    } else {
                        weatherDomain.getCurrentWeather(city = currentState.state.cityTextFieldState.value)
                            .collectLatest { locationData ->
                                when (locationData) {
                                    is DataState.Error -> DataState.Error(
                                        "Error loading weather data: ${locationData.message}"
                                    )

                                    DataState.Loading -> DataState.Loading
                                    is DataState.Success -> setLocationCoordinates(
                                        lat = locationData.state.lat,
                                        lon = locationData.state.lon
                                    )
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                if (currentState is DataState.Success) {

                    _dataState.value = DataState.Success(
                        currentState.state.copy(
                            cityTextFieldState = TextFieldState(
                                value = currentState.state.cityTextFieldState.value,
                                errorMessage = "Failed to update current city: ${e.message} \nPlease retry"
                            )
                        )
                    )
                }
            }
        }

        if (currentState is DataState.Error) {
            _dataState.value = DataState.Success(
                CurrentWeatherScreenState(
                    cityTextFieldState = TextFieldState(
                        errorMessage = "city name cannot be blank",
                        isError = true
                    )
                )
            )
        }
    }

    fun setCurrentCityCoordinateLocation() {
        val currentState = _dataState.value
        viewModelScope.launch {
            try {
                if (currentState is DataState.Success) {
                    if (currentState.state.latTextFieldState.value.isBlank()) {
                        _dataState.value = DataState.Success(
                            currentState.state.copy(
                                latTextFieldState = TextFieldState(
                                    value = currentState.state.latTextFieldState.value,
                                    errorMessage = "latitude cannot be blank",
                                    isError = true
                                )
                            )
                        )
                    } else if (currentState.state.lonTextFieldState.value.isBlank()) {
                        _dataState.value = DataState.Success(
                            currentState.state.copy(
                                lonTextFieldState = TextFieldState(
                                    value = currentState.state.lonTextFieldState.value,
                                    errorMessage = "longitude cannot be blank",
                                    isError = true
                                )
                            )
                        )
                    } else {
                        setLocationCoordinates(
                            lat = currentState.state.latTextFieldState.value.toDouble(),
                            lon = currentState.state.lonTextFieldState.value.toDouble()
                        )
                    }
                }
            } catch (e: Exception) {
                if (currentState is DataState.Success) {
                    _dataState.value =
                        DataState.Error(message = "Failed to get weather for coordinates: ${e.message} \nPlease retry")
                }
            }
        }

        if (currentState is DataState.Error) {
            _dataState.value = DataState.Success(
                CurrentWeatherScreenState(
                    cityTextFieldState = TextFieldState(
                        errorMessage = "city name cannot be blank",
                        isError = true
                    )
                )
            )
        }
    }

    fun onLocationTextValueChanged(newValue: String) {
        when (val currentState = _dataState.value) {
            is DataState.Error -> {

                _dataState.value = DataState.Success(
                    CurrentWeatherScreenState(
                        cityTextFieldState = TextFieldState(
                            value = newValue
                        )
                    )
                )
            }

            is DataState.Success -> {

                _dataState.value = DataState.Success(
                    currentState.state.copy(
                        cityTextFieldState = TextFieldState(
                            value = newValue
                        )
                    )
                )
            }

            DataState.Loading -> Unit
        }
    }

    fun onLocationLatValueChanged(newValue: String) {
        when (val currentState = _dataState.value) {
            is DataState.Error -> {

                _dataState.value = DataState.Success(
                    CurrentWeatherScreenState(
                        latTextFieldState = TextFieldState(
                            value = newValue
                        )
                    )
                )
            }

            is DataState.Success -> {

                _dataState.value = DataState.Success(
                    currentState.state.copy(
                        latTextFieldState = TextFieldState(
                            value = newValue
                        )
                    )
                )
            }

            DataState.Loading -> Unit
        }
    }

    fun onLocationLonValueChanged(newValue: String) {
        when (val currentState = _dataState.value) {
            is DataState.Error -> {

                _dataState.value = DataState.Success(
                    CurrentWeatherScreenState(
                        lonTextFieldState = TextFieldState(
                            value = newValue
                        )
                    )
                )
            }

            is DataState.Success -> {

                _dataState.value = DataState.Success(
                    currentState.state.copy(
                        lonTextFieldState = TextFieldState(
                            value = newValue
                        )
                    )
                )
            }

            DataState.Loading -> Unit
        }
    }

    fun getCurrentLocationCoordinates() {
        viewModelScope.launch {
            getCurrentLocationUseCase.execute()
                .catch { exception ->
                    _dataState.value = DataState.Error(
                        "Error loading device coordinates: ${exception.message ?: "Unknown error occurred"}"
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { (latitude, longitude) ->
                            setLocationCoordinates(lat = latitude, lon = longitude)
                        },
                        onFailure = { exception ->
                            _dataState.value = DataState.Error(
                                "Error loading device coordinates: ${exception.message ?: "Unknown error occurred"}"
                            )
                        }
                    )
                }
        }
    }
}