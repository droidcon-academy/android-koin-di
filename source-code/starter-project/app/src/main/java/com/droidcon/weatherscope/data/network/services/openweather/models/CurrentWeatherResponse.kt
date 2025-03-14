package com.droidcon.weatherscope.data.network.services.openweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentWeatherResponse(
    @Json(name = "coord")
    val coordinates: Coordinates,

    @Json(name = "weather")
    val weatherConditions: List<WeatherCondition>,

    @Json(name = "base")
    val base: String,

    @Json(name = "main")
    val main: MainWeatherData,

    @Json(name = "visibility")
    val visibility: Int,

    @Json(name = "wind")
    val wind: Wind,

    @Json(name = "rain")
    val rain: Rain?,

    @Json(name = "clouds")
    val clouds: Clouds,

    @Json(name = "dt")
    val dateTime: Long,

    @Json(name = "sys")
    val sys: SystemInfo,

    @Json(name = "timezone")
    val timezone: Int,

    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "cod")
    val cod: Int
)

@JsonClass(generateAdapter = true)
data class Coordinates(
    @Json(name = "lon")
    val longitude: Double,

    @Json(name = "lat")
    val latitude: Double
)

@JsonClass(generateAdapter = true)
data class WeatherCondition(
    @Json(name = "id")
    val id: Int,

    @Json(name = "main")
    val main: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "icon")
    val icon: String
)

@JsonClass(generateAdapter = true)
data class MainWeatherData(
    @Json(name = "temp")
    val temperature: Double,

    @Json(name = "feels_like")
    val feelsLike: Double,

    @Json(name = "temp_min")
    val tempMin: Double,

    @Json(name = "temp_max")
    val tempMax: Double,

    @Json(name = "pressure")
    val pressure: Int,

    @Json(name = "humidity")
    val humidity: Int,

    @Json(name = "sea_level")
    val seaLevel: Int?,

    @Json(name = "grnd_level")
    val groundLevel: Int?
)

@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "speed")
    val speed: Double,

    @Json(name = "deg")
    val degrees: Int,

    @Json(name = "gust")
    val gust: Double?
)

@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "1h")
    val oneHour: Double?
)

@JsonClass(generateAdapter = true)
data class Clouds(
    @Json(name = "all")
    val all: Int
)

@JsonClass(generateAdapter = true)
data class SystemInfo(
    @Json(name = "type")
    val type: Int?,

    @Json(name = "id")
    val id: Int?,

    @Json(name = "country")
    val country: String,

    @Json(name = "sunrise")
    val sunrise: Long,

    @Json(name = "sunset")
    val sunset: Long
)
