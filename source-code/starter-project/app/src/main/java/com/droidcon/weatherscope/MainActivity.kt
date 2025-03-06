package com.droidcon.weatherscope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.droidcon.weatherscope.ui.navigation.AppNavigation
import com.droidcon.weatherscope.ui.theme.WeatherScopeTheme
import com.droidcon.weatherscope.utils.AppPreferences
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