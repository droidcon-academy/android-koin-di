package com.droidcon.weatherscope.tests

import com.droidcon.weatherscope.di.domainModule
import com.droidcon.weatherscope.domain.GetCurrentWeatherUseCase
import com.droidcon.weatherscope.domain.WeatherDomain
import com.droidcon.weatherscope.domain.models.CurrentWeather
import com.droidcon.weatherscope.ui.common.DataState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkClass
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import java.net.URL
import kotlin.test.assertNotNull

class WeatherDomainTest : KoinTest {

    // Inject WeatherDomain from Koin's test container.
    private val weatherDomain: WeatherDomain by inject()
    private var useCaseMock: GetCurrentWeatherUseCase? = null

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
    val koinTestRule = KoinTestRule.create {
        modules(
            domainModule
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Before
    fun setup() {
        useCaseMock = declareMock<GetCurrentWeatherUseCase>()
    }

    @Test
    fun `should inject my components`() {
        assertNotNull(weatherDomain)
    }

    @Test
    fun `getCurrentWeather() by city returns flow of CurrentWeather data`() = runTest {
        // Arrange
        val cityName = "Colombo"
        val expectedData = weatherDataStub

        coEvery { useCaseMock?.getCurrentWeather(cityName) } returns flowOf(
            DataState.Success(expectedData)
        )

        // Act
        val resultFlow = weatherDomain.getCurrentWeather(cityName)
        val result = resultFlow.firstOrNull()

        // Assert
        assertThat(result).isInstanceOf(DataState.Success::class.java)
        val successResult = result as DataState.Success
        assertThat(successResult.state).isEqualTo(expectedData)

        // Verify interaction
        coVerify(exactly = 1) {
            useCaseMock?.getCurrentWeather(cityName)
        }
    }
}