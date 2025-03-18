package com.droidcon.weatherscope.data.database.forecast

import kotlinx.coroutines.flow.Flow

class WeatherForecastLocalSource(private val dao: WeatherForecastDao) {

    fun getForecasts(): Flow<List<WeatherForecastEntity>> = dao.getForecasts()

    suspend fun saveForecasts(forecasts: List<WeatherForecastEntity>) {
        deleteForecasts()
        dao.insertForecasts(forecasts)
    }

    suspend fun deleteForecasts() {
        dao.deleteAllForecasts()
    }
}