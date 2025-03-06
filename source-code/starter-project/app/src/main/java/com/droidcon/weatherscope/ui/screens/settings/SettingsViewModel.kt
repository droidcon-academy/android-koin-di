package com.droidcon.weatherscope.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.ui.common.ScreenState
import com.droidcon.weatherscope.ui.common.TextFieldState
import com.droidcon.weatherscope.utils.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(val appPreferences: AppPreferences) : ViewModel() {
    private val _screenState = MutableStateFlow<ScreenState<SettingsState>>(ScreenState.Loading)
    val screenState: StateFlow<ScreenState<SettingsState>> = _screenState

    init {
        viewModelScope.launch {
            combine(
                appPreferences.temperatureUnit,
                appPreferences.apiKey,
                appPreferences.isDarkTheme
            ) { temperatureUnit, apiKey, isDarkTheme ->
                SettingsState(
                    text = "Settings ViewModel Data",
                    apiKeyTextFieldState = TextFieldState(
                        value = apiKey,
                        isError = apiKey.isBlank(),
                        errorMessage = if (apiKey.isBlank()) "The App needs a valid api key to function, please enter your key." else null
                    ),
                    temperatureUnit = temperatureUnit,
                    darkThemeEnabled = isDarkTheme
                )
            }.catch { error ->
                _screenState.value = ScreenState.Error(error.message ?: "Unknown error occurred")
            }.collect { settingsState ->
                _screenState.value = ScreenState.Success(settingsState)
            }
        }
    }

    private fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            try {
                appPreferences.setTemperatureUnit(unit)
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error("Failed to update temperature unit: ${e.message}")
            }
        }
    }

   private fun setApiKey(key: String) {
        viewModelScope.launch {
            try {
                appPreferences.setApiKey(key)
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error("Failed to update API key: ${e.message}")
            }
        }
    }

    private fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setDarkTheme(enabled)
            } catch (e: Exception) {
                _screenState.value = ScreenState.Error("Failed to update theme: ${e.message}")
            }
        }
    }

    fun onApiKeyChanged(newValue: String) {
        val currentState = _screenState.value
        if (currentState is ScreenState.Success) {
            val isValid = newValue.isNotBlank()
            val errorMessage = if (!isValid) "API key cannot be empty" else null

            _screenState.value = ScreenState.Success(
                currentState.state.copy(
                    apiKeyTextFieldState = TextFieldState(
                        value = newValue,
                        isError = isValid,
                        errorMessage = errorMessage
                    )
                )
            )
        }
    }

    fun saveTemperatureUnit(tempUnit: String) {
        val currentState = _screenState.value
        if (currentState is ScreenState.Success) {
            _screenState.value = ScreenState.Success(
                currentState.state.copy(
                    temperatureUnit = tempUnit
                )
            )

            setTemperatureUnit(tempUnit)
        }
    }

    fun saveThemeSetting(isDarkTheme: Boolean) {
        val currentState = _screenState.value
        if (currentState is ScreenState.Success) {

            _screenState.value = ScreenState.Success(
                currentState.state.copy(
                    darkThemeEnabled = isDarkTheme
                )
            )

            setDarkTheme(isDarkTheme)
        }
    }

    fun saveApiKey() {
        val currentState = _screenState.value
        if (currentState is ScreenState.Success) {
            val apiKey = currentState.state.apiKeyTextFieldState.value
            if (apiKey.isNotBlank()) {
                setApiKey(apiKey)
            }
        }
    }
}