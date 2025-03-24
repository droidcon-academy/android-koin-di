package com.droidcon.weatherscope.ui.screens.settings

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.droidcon.weatherscope.R
import com.droidcon.weatherscope.common.TemperatureUnit
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.ui.navigation.Routes
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(selectedCity: String, onBack: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel(parameters = { parametersOf(selectedCity) })
    val state by viewModel.dataState.collectAsState()
    val screenState = state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            when (screenState) {
                is DataState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DataState.Success -> {
                    val settingsState = screenState.state

                    // Temperature Unit Toggle Section
                    Text(
                        stringResource(R.string.temperature_unit),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = settingsState.temperatureUnit == TemperatureUnit.CELSIUS,
                            onClick = { viewModel.saveTemperatureUnit(TemperatureUnit.CELSIUS) }
                        )
                        Text(stringResource(R.string.celsius))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = settingsState.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                            onClick = { viewModel.saveTemperatureUnit(TemperatureUnit.FAHRENHEIT) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.fahrenheit))
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Dark/Light Mode Toggle Section
                    Text(
                        stringResource(R.string.theme),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = settingsState.darkThemeEnabled,
                            onCheckedChange = { viewModel.saveThemeSetting(!settingsState.darkThemeEnabled) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (settingsState.darkThemeEnabled) stringResource(R.string.dark_mode) else stringResource(
                                R.string.light_mode
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Api Key Section
                    Text(
                        stringResource(R.string.weather_api_key),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = settingsState.apiKeyTextFieldState.value,
                            onValueChange = { viewModel.onApiKeyChanged(it) },
                            isError = settingsState.apiKeyTextFieldState.isError,
                            supportingText = {
                                Text(text = settingsState.apiKeyTextFieldState.errorMessage ?: "")
                            },
                            label = { Text(stringResource(R.string.enter_key)) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.saveApiKey()
                        }) {
                            Text(stringResource(R.string.set))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Selected city Section
                    if (settingsState.apiKeyTextFieldState.value.isNotEmpty()) {
                        Text(
                            stringResource(R.string.title_selected_city),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(settingsState.selectedCity)
                    }
                }

                is DataState.Error -> {
                    Text(
                        text = stringResource(R.string.error_generic, screenState.message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}