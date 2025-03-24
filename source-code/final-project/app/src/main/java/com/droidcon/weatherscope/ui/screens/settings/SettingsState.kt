package com.droidcon.weatherscope.ui.screens.settings

import com.droidcon.weatherscope.common.TemperatureUnit
import com.droidcon.weatherscope.ui.common.TextFieldState

data class SettingsState(
    val selectedCity: String,
    val apiKeyTextFieldState: TextFieldState = TextFieldState(),
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val darkThemeEnabled: Boolean = false
    )
