package com.droidcon.weatherscope.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.droidcon.weatherscope.common.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val APP_PREFERENCES_NAME = "app_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCES_NAME)

val utilsModule = module {
    single<DataStore<Preferences>> { androidContext().dataStore }
    single { AppPreferences(dataStore = get()) }
}