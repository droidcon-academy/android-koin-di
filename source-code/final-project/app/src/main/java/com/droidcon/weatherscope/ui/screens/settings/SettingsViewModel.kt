package com.droidcon.weatherscope.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.weatherscope.R
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.ui.common.TextFieldState
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.StringResourcesProvider
import com.droidcon.weatherscope.common.TemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(private val appPreferences: AppPreferences, private val stringResourcesProvider: StringResourcesProvider, private val selectedCity: String) : ViewModel() {
    private val _dataState = MutableStateFlow<DataState<SettingsState>>(DataState.Loading)
    val dataState: StateFlow<DataState<SettingsState>> = _dataState

    init {
        viewModelScope.launch {
            combine(
                appPreferences.temperatureUnit,
                appPreferences.apiKey,
                appPreferences.isDarkTheme
            ) { temperatureUnit, apiKey, isDarkTheme ->
                SettingsState(
                    selectedCity = selectedCity,
                    apiKeyTextFieldState = TextFieldState(
                        value = apiKey,
                        isError = apiKey.isBlank(),
                        errorMessage = if (apiKey.isBlank()) stringResourcesProvider.getString(R.string.the_app_needs_a_valid_api_key_to_function_please_enter_your_key) else null
                    ),
                    temperatureUnit = temperatureUnit,
                    darkThemeEnabled = isDarkTheme
                )
            }.catch { error ->
                _dataState.value = DataState.Error(error.message ?: stringResourcesProvider.getString(R.string.unknown_error))
            }.collect { settingsState ->
                _dataState.value = DataState.Success(settingsState)
            }
        }
    }

    private fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            try {
                appPreferences.setTemperatureUnit(unit)
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    stringResourcesProvider.getString(
                        R.string.failed_to_update_temperature_unit,
                        e.message ?: ""
                    ))
            }
        }
    }

   private fun setApiKey(key: String) {
        viewModelScope.launch {
            try {
                appPreferences.setApiKey(key)
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    stringResourcesProvider.getString(
                        R.string.failed_to_update_api_key,
                        e.message ?: stringResourcesProvider.getString(R.string.unknown_error)
                    ))
            }
        }
    }

    private fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setDarkTheme(enabled)
            } catch (e: Exception) {
                _dataState.value = DataState.Error(
                    stringResourcesProvider.getString(
                        R.string.failed_to_update_theme,
                        e.message ?: stringResourcesProvider.getString(R.string.unknown_error)
                    ))
            }
        }
    }

    fun onApiKeyChanged(newValue: String) {
        val currentState = _dataState.value
        if (currentState is DataState.Success) {
            val isValid = newValue.isNotBlank()
            val errorMessage = if (!isValid) stringResourcesProvider.getString(R.string.api_key_cannot_be_empty) else null

            _dataState.value = DataState.Success(
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

    fun saveTemperatureUnit(tempUnit: TemperatureUnit) {
        val currentState = _dataState.value
        if (currentState is DataState.Success) {
            _dataState.value = DataState.Success(
                currentState.state.copy(
                    temperatureUnit = tempUnit
                )
            )

            setTemperatureUnit(tempUnit)
        }
    }

    fun saveThemeSetting(isDarkTheme: Boolean) {
        val currentState = _dataState.value
        if (currentState is DataState.Success) {

            _dataState.value = DataState.Success(
                currentState.state.copy(
                    darkThemeEnabled = isDarkTheme
                )
            )

            setDarkTheme(isDarkTheme)
        }
    }

    fun saveApiKey() {
        val currentState = _dataState.value
        if (currentState is DataState.Success) {
            val apiKey = currentState.state.apiKeyTextFieldState.value
            if (apiKey.isNotBlank()) {
                setApiKey(apiKey)
            }
        }
    }
}