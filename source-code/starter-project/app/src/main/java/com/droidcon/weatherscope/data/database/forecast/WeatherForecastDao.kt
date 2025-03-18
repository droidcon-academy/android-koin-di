package com.droidcon.weatherscope.data.database.forecast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherForecastDao {

    @Query("SELECT * FROM weather_forecast ORDER BY date ASC")
    fun getForecasts(): Flow<List<WeatherForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<WeatherForecastEntity>)

    @Query("DELETE FROM weather_forecast")
    suspend fun deleteAllForecasts()
}