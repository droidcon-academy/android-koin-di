package com.droidcon.weatherscope.ui.screens.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.TemperatureUnit
import com.droidcon.weatherscope.common.toFahrenheit
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.models.WeatherForecastItem
import com.droidcon.weatherscope.ui.common.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class ForecastViewModel(
    private val appPreferences: AppPreferences,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase
) : ViewModel() {

    private val _dataState = MutableStateFlow<DataState<ForecastScreenState>>(DataState.Loading)
    val dataState: StateFlow<DataState<ForecastScreenState>> = _dataState

    init {
        loadForecast()
    }

    private fun WeatherForecastItem.toUiState(temperatureUnit: TemperatureUnit): WeatherForecastItemUiState {
        val formattedMinTemp = when (temperatureUnit) {
            TemperatureUnit.CELSIUS -> "${tempMin.roundToInt()}°C"
            TemperatureUnit.FAHRENHEIT -> "${tempMin.toFahrenheit().roundToInt()}°F"
        }

        val formattedMaxTemp = when (temperatureUnit) {
            TemperatureUnit.CELSIUS -> "${tempMax.roundToInt()}°C"
            TemperatureUnit.FAHRENHEIT -> "${tempMax.toFahrenheit().roundToInt()}°F"
        }

        val formattedDate = date.format(DateTimeFormatter.ofPattern("EEE, MMM d"))

        return WeatherForecastItemUiState(
            date = formattedDate,
            weatherDescription = weatherDescription,
            tempMin = formattedMinTemp,
            tempMax = formattedMaxTemp,
            iconLink = iconLink,
            temperatureUnit = temperatureUnit
        )
    }

    private fun applyTemperatureUnit(
        forecast: WeatherForecastItem,
        unit: TemperatureUnit
    ): WeatherForecastItem {
        return when (unit) {
            TemperatureUnit.CELSIUS -> forecast // Already in Celsius, no conversion needed
            TemperatureUnit.FAHRENHEIT -> {
                forecast.copy(
                    tempMin = forecast.tempMin.toFahrenheit(),
                    tempMax = forecast.tempMax.toFahrenheit()
                )
            }
        }
    }

    fun loadForecast() {
        _dataState.value = DataState.Loading

        viewModelScope.launch {
            try {
                val coordinatesFlow = combine(
                    appPreferences.currentCityLat.distinctUntilChanged(),
                    appPreferences.currentCityLon.distinctUntilChanged()
                ) { lat, lon -> Pair(lat, lon) }

                // For every new coordinate pair, fetch forecast from the domain
                val forecastFlow =
                    coordinatesFlow.distinctUntilChanged().flatMapLatest { (lat, lon) ->
                        getWeatherForecastUseCase.getWeatherForecast(
                            latitude = lat,
                            longitude = lon
                        )
                    }

                combine(
                    forecastFlow,
                    appPreferences.temperatureUnit.distinctUntilChanged()
                ) { forecastData, tempUnit ->
                    Pair(forecastData, tempUnit)
                }
                    .catch { exception ->
                        _dataState.value = DataState.Error(exception.message ?: "Unknown error")
                    }
                    .collect { (forecastData, tempUnit) ->
                        _dataState.value = when (forecastData) {
                            is DataState.Error -> DataState.Error(
                                "Error loading forecast data: ${forecastData.message}"
                            )

                            DataState.Loading -> DataState.Loading
                            is DataState.Success -> {
                                // Convert each forecast item with the appropriate temperature unit
                                val convertedForecasts = forecastData.state.map { forecast ->
                                    applyTemperatureUnit(forecast, tempUnit)
                                }

                                DataState.Success(
                                    state = ForecastScreenState(
                                        forecasts = convertedForecasts.map { it.toUiState(tempUnit) },
                                        temperatureUnit = tempUnit
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
}