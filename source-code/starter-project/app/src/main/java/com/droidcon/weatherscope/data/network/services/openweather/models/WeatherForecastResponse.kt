package com.droidcon.weatherscope.data.network.services.openweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecastResponse(
    @Json(name = "cod")
    val cod: String,

    @Json(name = "message")
    val message: Int,

    @Json(name = "cnt")
    val count: Int,

    @Json(name = "list")
    val forecastItems: List<ForecastItem>,

    @Json(name = "city")
    val city: City
) {
    @JsonClass(generateAdapter = true)
    data class ForecastItem(
        @Json(name = "dt")
        val dateTime: Long,

        @Json(name = "main")
        val main: MainWeatherData,

        @Json(name = "weather")
        val weatherConditions: List<WeatherCondition>,

        @Json(name = "clouds")
        val clouds: Clouds?,

        @Json(name = "wind")
        val wind: Wind?,

        @Json(name = "visibility")
        val visibility: Int,

        @Json(name = "pop")
        val probabilityOfPrecipitation: Double,

        @Json(name = "rain")
        val rain: Rain?,

        @Json(name = "sys")
        val sys: ForecastSys,

        @Json(name = "dt_txt")
        val dateTimeText: String
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

        @Json(name = "sea_level")
        val seaLevel: Int,

        @Json(name = "grnd_level")
        val groundLevel: Int,

        @Json(name = "humidity")
        val humidity: Int,

        @Json(name = "temp_kf")
        val tempKf: Double
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
    data class Clouds(
        @Json(name = "all")
        val all: Int
    )

    @JsonClass(generateAdapter = true)
    data class Wind(
        @Json(name = "speed")
        val speed: Double,

        @Json(name = "deg")
        val deg: Int,

        @Json(name = "gust")
        val gust: Double
    )

    @JsonClass(generateAdapter = true)
    data class Rain(
        @Json(name = "3h")
        val threeHour: Double
    )

    @JsonClass(generateAdapter = true)
    data class ForecastSys(
        @Json(name = "pod")
        val pod: String
    )

    @JsonClass(generateAdapter = true)
    data class City(
        @Json(name = "id")
        val id: Long,

        @Json(name = "name")
        val name: String,

        @Json(name = "coord")
        val coordinates: Coordinates,

        @Json(name = "country")
        val country: String,

        @Json(name = "population")
        val population: Int,

        @Json(name = "timezone")
        val timezone: Int,

        @Json(name = "sunrise")
        val sunrise: Long,

        @Json(name = "sunset")
        val sunset: Long
    )

    @JsonClass(generateAdapter = true)
    data class Coordinates(
        @Json(name = "lat")
        val latitude: Double,

        @Json(name = "lon")
        val longitude: Double
    )
}

