package com.droidcon.weatherscope.data.database.forecast

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_forecast")
data class WeatherForecastEntity(
    @PrimaryKey val date: String,
    @ColumnInfo(name = "weather_description") val weatherDescription: String,
    @ColumnInfo(name = "temp_min") val tempMin: Double,
    @ColumnInfo(name = "temp_max") val tempMax: Double,
    @ColumnInfo(name = "icon_link") val iconLink: String,
    // Timestamp in millis when the forecast was cached
    @ColumnInfo(name = "fetched_at") val fetchedAt: Long
)
