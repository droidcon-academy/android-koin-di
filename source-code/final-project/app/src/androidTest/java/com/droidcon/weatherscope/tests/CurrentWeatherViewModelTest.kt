package com.droidcon.weatherscope.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.droidcon.weatherscope.KoinTestRule
import com.droidcon.weatherscope.di.DataModule
import com.droidcon.weatherscope.di.DomainModule
import com.droidcon.weatherscope.di.NetworkModule
import com.droidcon.weatherscope.di.UtilsModule
import com.droidcon.weatherscope.di.viewModelModule
import com.droidcon.weatherscope.ui.common.DataState
import com.droidcon.weatherscope.ui.screens.currentweather.CurrentWeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class CurrentWeatherViewModelTest : KoinTest {
    // Inject the view model using Koin's inject (or get())
    private val viewModel: CurrentWeatherViewModel by inject()

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            viewModelModule,
            UtilsModule.module,
            NetworkModule.module,
            DomainModule.module,
            DataModule.module
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test_current_weather_view_model_loads_successfully`() = runTest {

        val state = viewModel.dataState.value
        advanceUntilIdle()

        // Assert that state is Loading after view model starts
        assertTrue("Expected Loading state", state is DataState.Loading)
    }
}