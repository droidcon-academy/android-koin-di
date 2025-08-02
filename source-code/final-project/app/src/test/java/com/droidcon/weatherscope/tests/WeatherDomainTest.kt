package com.droidcon.weatherscope.tests

import com.droidcon.weatherscope.di.DomainModule
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
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import java.net.URL
import kotlin.test.assertNotNull

class WeatherDomainTest : KoinTest {

    // Inject WeatherDomain from Koin's test container.
    private val weatherDomain: WeatherDomain by inject() // (1)
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
        // Provide the module that contains WeatherDomain and its dependencies
        modules(DomainModule.module)
    } // (2)

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz) // (3) Provide a way to create mocks for any class using MockK
    }

    @Before
    fun setup() {
        // Instead of using the real GetCurrentWeatherUseCase, provide a mock for it
        useCaseMock = declareMock<GetCurrentWeatherUseCase>() // (4)
    }

    @Test
    fun `should inject my components`() {
        // Now WeatherDomain should be injected with a mock GetCurrentWeatherUseCase
        assertNotNull(weatherDomain) // (5) Verify that the WeatherDomain instance was provided
    }

    @Test
    fun `getCurrentWeather() by city returns flow of CurrentWeather data`() = runTest {
        // (6) Test that the weatherDomain correctly uses the mocked use case
        // to emit a flow containing a success result with expected weather data.

        // Arrange: Prepare city name and expected stubbed result
        val cityName = "Colombo"
        val expectedData = weatherDataStub

        // Stub the mocked use case to return a success flow when queried with cityName
        coEvery { useCaseMock?.getCurrentWeather(cityName) } returns flowOf(
            DataState.Success(expectedData)
        )

        // Act: Call the method under test
        val resultFlow = weatherDomain.getCurrentWeather(cityName)
        val result = resultFlow.firstOrNull()

        // Assert: Check that the emitted value is a DataState.Success with the expected data
        assertThat(result).isInstanceOf(DataState.Success::class.java)
        val successResult = result as DataState.Success
        assertThat(successResult.state).isEqualTo(expectedData)

        // Verify that the mocked use case was called exactly once with the expected city
        coVerify(exactly = 1) {
            useCaseMock?.getCurrentWeather(cityName)
        }
    }
}