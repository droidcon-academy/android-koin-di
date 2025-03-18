package com.droidcon.weatherscope.data.repositories

import android.util.Log
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastLocalSource
import com.droidcon.weatherscope.data.database.forecast.toDomain
import com.droidcon.weatherscope.data.database.forecast.toEntity
import com.droidcon.weatherscope.data.network.services.openweather.WeatherApiService
import com.droidcon.weatherscope.data.network.services.openweather.models.CurrentWeatherResponse
import com.droidcon.weatherscope.data.network.services.openweather.models.WeatherForecastResponse
import com.droidcon.weatherscope.data.network.utils.ApiCallResult
import com.droidcon.weatherscope.data.network.utils.safeApiCall
import com.droidcon.weatherscope.domain.models.WeatherForecastItem
import com.droidcon.weatherscope.domain.toDomain
import com.droidcon.weatherscope.ui.common.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WeatherRepository(
    private val apiService: WeatherApiService,
    private val localSource: WeatherForecastLocalSource
) {

    fun getCurrentWeatherByCity(city: String): Flow<ApiCallResult<CurrentWeatherResponse>> =
        safeApiCall {
            apiService.getCurrentWeatherByCity(city)
        }

    fun getCurrentWeatherByCoordinates(
        lat: Double,
        lon: Double
    ): Flow<ApiCallResult<CurrentWeatherResponse>> =
        safeApiCall {
            apiService.getCurrentWeatherByCoordinates(lat, lon)
        }.onEach { result ->
            when (result) {
                is ApiCallResult.Success -> { // Get forecast data
                    CoroutineScope(Dispatchers.IO).launch {
                        getWeatherForecastByCoordinates(lat, lon).collect { forecastResult ->
                            when (forecastResult) {
                                is ApiCallResult.Success -> { // Cache forecast data
                                    val domainForecasts = forecastResult.response.toDomain()
                                    val forecastEntities = domainForecasts.map { it.toEntity() }
                                    localSource.saveForecasts(forecastEntities)
                                }

                                is ApiCallResult.Error -> {
                                    localSource.deleteForecasts()
                                }

                                else -> Unit
                            }
                        }
                    }
                }

                else -> Unit
            }
        }

    private fun getWeatherForecastByCoordinates(
        lat: Double,
        lon: Double
    ): Flow<ApiCallResult<WeatherForecastResponse>> =
        safeApiCall {
            apiService.getForecastByCoordinates(lat, lon)
        }

    fun getWeatherForecast(
        latitude: Double,
        longitude: Double
    ): Flow<DataState<List<WeatherForecastItem>>> =
        channelFlow {
            localSource.getForecasts().collectLatest { localForecasts ->
                Log.d("Mane", "localForecasts: ${localForecasts}")
                val currentTime = System.currentTimeMillis()


                // Determine freshness (all records are considered fresh if they were fetched within the last 3 hours)
                val isFresh = localForecasts.isNotEmpty() && localForecasts.all {
                    (currentTime - it.fetchedAt) < (3 * 60 * 60 * 1000) // 3 hours in millis
                }

                if (localForecasts.isNotEmpty() && isFresh) {
                    send(DataState.Success(localForecasts.map { it.toDomain() }))
                } else { // Fallback to remote API call.
                    try {
                        val apiResponse = apiService.getForecastByCoordinates(latitude, longitude)
                        val domainForecasts = apiResponse.toDomain()
                        val forecastEntities = domainForecasts.map { it.toEntity() }
                        localSource.saveForecasts(forecastEntities)
                        send(DataState.Success(domainForecasts))
                    } catch (e: Exception) {
                        send(DataState.Error("While loading forecasts from api, ${e.message ?: "Unknown error"}"))
                    }
                }
            }
        }.catch { exception ->
            emit(DataState.Error("While loading local cache, ${exception.message ?: "Unknown error"}"))
        }
}