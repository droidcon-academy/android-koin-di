package com.droidcon.weatherscope.ui.screens.settings

import com.droidcon.weatherscope.ui.common.TextFieldState

data class SettingsState(
    val text: String,
    val apiKeyTextFieldState: TextFieldState = TextFieldState(),
    val temperatureUnit: String = "Celsius",
    val darkThemeEnabled: Boolean = false
    )
