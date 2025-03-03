package com.droidcon.weatherscope.ui.screens.currentweather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidcon.weatherscope.ui.common.ScreenState

@Composable
fun CurrentWeatherScreen(
    onNavigateToForecast: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: CurrentWeatherViewModel = CurrentWeatherViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val screenState = state
    var city by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (screenState) {
            is ScreenState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ScreenState.Success -> {
                val weatherState = screenState.state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = weatherState.text)
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
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Trigger GPS lookup and call viewModel.loadWeather(...) with coordinates */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use Current GPS")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Enter City") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { }) {
                Text("Search")
            }
        }
        Spacer(modifier = Modifier.weight(2f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onNavigateToForecast) {
                Text("Forecast")
            }
            Button(onClick = onNavigateToSettings) {
                Text("Settings")
            }
        }
    }
}