package com.droidcon.weatherscope.ui.screens.currentweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.R
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.ui.common.TextFieldState
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.StringResourcesProvider
import com.droidcon.weatherscope.common.TemperatureUnit
import com.droidcon.weatherscope.common.toFahrenheit
import com.droidcon.weatherscope.domain.models.CurrentWeather
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
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val stringResourcesProvider: StringResourcesProvider
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
                        _dataState.value = DataState.Error(
                            exception.message
                                ?: stringResourcesProvider.getString(R.string.unknown_error)
                        )
                    }
                    .collect { (weatherData, tempUnit) ->
                        _dataState.value = when (weatherData) {
                            is DataState.Error -> DataState.Error(
                                stringResourcesProvider.getString(
                                    R.string.error_loading_weather_data,
                                    weatherData.message
                                )
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
                _dataState.value = DataState.Error(
                    e.message ?: stringResourcesProvider.getString(R.string.unknown_error)
                )
            }
        }
    }

    private suspend fun setLocationCoordinates(lat: Double, lon: Double) {
        try {
            appPreferences.setCurrentCityLat(lat)
            appPreferences.setCurrentCityLon(lon)
        } catch (e: Exception) {
            _dataState.value =
                DataState.Error(
                    stringResourcesProvider.getString(
                        R.string.error_saving_lat_lon,
                        (e.message ?: stringResourcesProvider.getString(R.string.unknown_error))
                    )
                )
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
                                    errorMessage = stringResourcesProvider.getString(R.string.city_name_cannot_be_blank),
                                    isError = true
                                )
                            )
                        )
                    } else {
                        weatherDomain.getCurrentWeather(city = currentState.state.cityTextFieldState.value)
                            .collectLatest { locationData ->
                                when (locationData) {
                                    is DataState.Error -> {
                                        _dataState.value = DataState.Error(
                                            stringResourcesProvider.getString(
                                                R.string.error_loading_weather_data,
                                                locationData.message
                                            )
                                        )
                                    }

                                    DataState.Loading -> {
                                        _dataState.value = DataState.Loading
                                    }

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
                        errorMessage = stringResourcesProvider.getString(R.string.city_name_cannot_be_blank),
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

    fun getCurrentLocationCoordinates() {
        viewModelScope.launch {
            getCurrentLocationUseCase.execute()
                .catch { exception ->
                    _dataState.value = DataState.Error(
                        stringResourcesProvider.getString(
                            R.string.error_loading_device_coordinates,
                            exception.message ?: stringResourcesProvider.getString(R.string.unknown_error)
                        )
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { (latitude, longitude) ->
                            setLocationCoordinates(lat = latitude, lon = longitude)
                        },
                        onFailure = { exception ->
                            _dataState.value = DataState.Error(
                                stringResourcesProvider.getString(
                                    R.string.error_loading_device_coordinates,
                                    exception.message ?: stringResourcesProvider.getString(R.string.unknown_error)
                                )
                            )
                        }
                    )
                }
        }
    }
}