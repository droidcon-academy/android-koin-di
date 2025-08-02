package com.droidcon.weatherscope.ui.navigation

object Routes {
    const val SPLASH = "splash_screen"
    const val CURRENT_WEATHER = "current_weather_screen"
    const val FORECAST = "forecast_screen"
    const val SETTINGS = "settings_screen"

    const val SETTINGS_WITH_PARAM = "$SETTINGS/{paramName}"

    fun createSettingsRoute(paramValue: String): String {
        return "$SETTINGS/$paramValue"
    }
}