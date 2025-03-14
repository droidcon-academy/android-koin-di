package com.droidcon.weatherscope.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TEMP_UNIT_KEY = stringPreferencesKey("temperature_unit")
        private val API_KEY = stringPreferencesKey("api_key")
        private val THEME_MODE_KEY = booleanPreferencesKey("theme_mode")
        private val CITY_NAME = stringPreferencesKey("city_text")
    }

    val temperatureUnit: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[TEMP_UNIT_KEY] ?: "Celsius" } // default : "Celsius"

    val apiKey: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[API_KEY] ?: "" } // default : empty string

    val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[THEME_MODE_KEY] ?: false } // default : false

    val currentCityName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences[CITY_NAME] ?: "" } // default : empty string

    suspend fun setTemperatureUnit(unit: String) {
        dataStore.edit { preferences ->
            preferences[TEMP_UNIT_KEY] = unit
        }
    }

    suspend fun setApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[API_KEY] = key
        }
    }

    // pass true for dark mode.
    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = enabled
        }
    }

    suspend fun setCurrentCityName(key: String) {
        dataStore.edit { preferences ->
            preferences[CITY_NAME] = key
        }
    }
}