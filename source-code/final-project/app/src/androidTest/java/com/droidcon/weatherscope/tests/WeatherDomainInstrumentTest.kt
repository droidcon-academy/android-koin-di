package com.droidcon.weatherscope.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.droidcon.weatherscope.KoinTestRule
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.GetWeatherForecastUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.domain.models.CurrentWeather
import com.droidcon.weatherscope.ui.common.DataState
import com.google.common.truth.Truth
import io.mockk.mockkClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import java.net.URL

@RunWith(AndroidJUnit4::class)
class WeatherDomainInstrumentTest: KoinTest {
    private val weatherDomain: WeatherDomain by inject()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    private val instrumentedTestModule = module {
        factory<GetCurrentWeatherUseCase> {
            declareMock {}
        }

        factory<WeatherDomain> {
            WeatherDomain(object : GetCurrentWeatherUseCase {
                override  fun getCurrentWeather(cityName: String): Flow<DataState<CurrentWeather>> {
                    return flowOf(DataState.Success(weatherDataStub))
                }

                override  fun getCurrentWeather(
                    latitude: Double,
                    longitude: Double
                ): Flow<DataState<CurrentWeather>> {
                    return flowOf(DataState.Success(weatherDataStub))
                }
            })
        }

        factory<GetWeatherForecastUseCase> {
            declareMock {}
        }
    }

    private val weatherDataStub = CurrentWeather(
        locationName = "Colombo",
        lat = 1.22,
        lon = 0.45,
        status = "Sunny",
        description = "Clear skies",
        temp = 23.5,
        humidity = 1.00,
        iconLink = URL("https://openweathermap.org")
    )

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            instrumentedTestModule, // instead of domainModule
        )
    )

    @Test
    fun `getCurrentWeather_by_city_returns_flow_of_CurrentWeather_data`() = runTest {
        // Arrange
        val cityName = "Colombo"

        // Act
        val resultFlow = weatherDomain.getCurrentWeather(cityName)
        val result = resultFlow.firstOrNull()

        // Assert
        Truth.assertThat(result).isInstanceOf(DataState.Success::class.java)
        val successResult = result as DataState.Success
        Truth.assertThat(successResult.state.description).isEqualTo("Clear skies")
    }
}