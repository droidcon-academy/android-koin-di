package com.droidcon.weatherscope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.droidcon.weatherscope.ui.navigation.AppNavigation
import com.droidcon.weatherscope.ui.theme.WeatherScopeTheme
import com.droidcon.weatherscope.common.AppPreferences
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val appPreferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme by appPreferences.isDarkTheme.collectAsState(initial = false)

            WeatherScopeTheme(darkThemeEnabled = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}