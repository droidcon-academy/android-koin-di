package com.droidcon.weatherscope.common

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    companion object {
        fun fromString(value: String?): TemperatureUnit {
            return when (value) {
                FAHRENHEIT.name -> FAHRENHEIT
                else -> CELSIUS // Default to Celsius
            }
        }
    }
}