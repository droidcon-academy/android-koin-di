package com.droidcon.weatherscope.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.droidcon.weatherscope.R
import com.droidcon.weatherscope.ui.common.ScreenState

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = SettingsViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val screenState = state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
    ) {
        when (screenState) {
            is ScreenState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ScreenState.Success -> {
                val settingsState = screenState.state

                // Temperature Unit Toggle Section
                Text("Temperature Unit", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settingsState.temperatureUnit == "Celsius",
                        onClick = { }
                    )
                    Text("Celsius")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = settingsState.temperatureUnit == "Fahrenheit",
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fahrenheit")
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Dark/Light Mode Toggle Section
                Text("Theme", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = settingsState.darkThemeEnabled,
                        onCheckedChange = { }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (settingsState.darkThemeEnabled) "Dark Mode" else "Light Mode")
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Dark/Light Mode Toggle Section
                Text("Weather Api Key", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("Enter Key") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { }) {
                        Text("Set")
                    }
                }
            }

            is ScreenState.Error -> {
                Text(
                    text = "Error: ${screenState.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}