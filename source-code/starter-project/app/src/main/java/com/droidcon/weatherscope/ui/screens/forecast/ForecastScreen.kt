package com.droidcon.weatherscope.ui.screens.forecast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    onBack: () -> Unit,
    viewModel: ForecastViewModel = ForecastViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val screenState = state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "5-Day Forecast") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(16.dp).padding(paddingValues)) {
            when (screenState) {
                is ScreenState.Loading -> {
                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ScreenState.Success -> {
                        Text(text = screenState.state.text)
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
}