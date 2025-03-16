package com.droidcon.weatherscope.ui.screens.currentweather

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.droidcon.weatherscope.R
import com.droidcon.weatherscope.ui.common.DataState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(
    onNavigateToForecast: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: CurrentWeatherViewModel = koinViewModel()
    val state by viewModel.dataState.collectAsState()
    val screenState = state
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Scope") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (screenState) {
                is DataState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DataState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = screenState.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                is DataState.Success -> {
                    val weatherState = screenState.state
                    weatherState.data?.let { WeatherCard(weatherData = it) }
                }
            }

            val weatherState = (screenState as? DataState.Success)?.state

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.getCurrentLocationCoordinates() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use Current GPS")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column {
                TextField(
                    value = weatherState?.latTextFieldState?.value ?: "",
                    onValueChange = { viewModel.onLocationLatValueChanged(it) },
                    isError = weatherState?.latTextFieldState?.isError ?: false,
                    supportingText = {
                        Text(text = weatherState?.latTextFieldState?.errorMessage ?: "")
                    },
                    label = { Text("Latitude") },
                    modifier = Modifier.width(480.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = weatherState?.lonTextFieldState?.value ?: "",
                        onValueChange = { viewModel.onLocationLonValueChanged(it) },
                        isError = weatherState?.lonTextFieldState?.isError ?: false,
                        supportingText = {
                            Text(text = weatherState?.lonTextFieldState?.errorMessage ?: "")
                        },
                        label = { Text("Longitude") },
                        modifier = Modifier.width(480.dp).weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.setCurrentCityCoordinateLocation() }) {
                        Text("Search")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = weatherState?.cityTextFieldState?.value ?: "",
                    onValueChange = { viewModel.onLocationTextValueChanged(it) },
                    isError = weatherState?.cityTextFieldState?.isError ?: false,
                    supportingText = {
                        Text(text = weatherState?.cityTextFieldState?.errorMessage ?: "")
                    },
                    label = { Text("Enter City") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.setCurrentCityNameLocation() }) {
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
}

@Composable
fun WeatherCard(
    weatherData: CurrentWeatherUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Location and status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location and weather status
                Column {
                    Text(
                        text = weatherData.locationName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weatherData.status,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Weather icon
                AsyncImage(
                    model = weatherData.iconLink.toString(),
                    contentDescription = weatherData.description,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.ic_placeholder_weather),
                    error = painterResource(id = R.drawable.ic_broken_image)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Weather description
            Text(
                text = weatherData.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Temperature and humidity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Temperature
                WeatherInfoItem(
                    icon = Icons.Filled.Info,
                    label = "Temperature",
                    value = weatherData.temperature
                )

                // Humidity
                WeatherInfoItem(
                    icon = Icons.Filled.Info,
                    label = "Humidity",
                    value = weatherData.humidity
                )
            }
        }
    }
}

@Composable
fun WeatherInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}