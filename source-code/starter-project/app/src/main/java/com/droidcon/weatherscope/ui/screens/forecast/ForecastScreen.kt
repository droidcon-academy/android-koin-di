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
import com.droidcon.weatherscope.ui.common.DataState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForecastScreen() {
    val viewModel: ForecastViewModel = koinViewModel()
    val state by viewModel.dataState.collectAsState()
    val screenState = state

    Column(modifier = Modifier.padding(16.dp)) {
        when (screenState) {
            is DataState.Loading -> {
                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DataState.Success -> {
                Text(text = screenState.state.text)
            }

            is DataState.Error -> {
                Text(
                    text = "Error: ${screenState.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}