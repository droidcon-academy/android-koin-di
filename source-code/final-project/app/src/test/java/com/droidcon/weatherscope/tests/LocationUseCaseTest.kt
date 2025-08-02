package com.droidcon.weatherscope.tests

import androidx.datastore.core.DataStore
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.PermissionChecker
import com.droidcon.weatherscope.common.StringResourcesProvider
import com.droidcon.weatherscope.di.UtilsModule
import io.mockk.mockkClass
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declare
import org.koin.test.mock.declareMock
import java.util.prefs.Preferences

class LocationUseCaseTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Before
    fun setup() {
        // Manually start Koin with the module(s) needed for this test
        startKoin {
            modules(UtilsModule.module)
        } // (1)

        // Provide a fake implementation for GetCurrentLocationUseCase just for this test
        declare<GetCurrentLocationUseCase> {
            FakeGetCurrentLocationUseCase()
        } // (2)

        // Declare additional mocks required by the use case or its dependencies
        declareMock<DataStore<Preferences>>()
        declareMock<AppPreferences>()
        declareMock<PermissionChecker>()
        declareMock<StringResourcesProvider>()
    }

    @After
    fun tearDown() {
        stopKoin() // (3) Clean up the Koin context after each test
    }

    @Test
    fun testFakeLocationUseCase() = runBlocking {
        // Retrieve the use case from Koin (it will return our fake implementation)
        val useCase: GetCurrentLocationUseCase by inject()

        // Use the fake use case and verify its behavior
        val resultFlow = useCase.execute()
        val result = resultFlow.single()

        // The fake use case always returns (0.0, 0.0) wrapped in Success
        assertTrue(result.isSuccess && result.getOrNull() == Pair(0.0, 0.0))
    }
}

// A fake implementation of GetCurrentLocationUseCase for testing purposes
class FakeGetCurrentLocationUseCase : GetCurrentLocationUseCase {
    override fun execute(): Flow<Result<Pair<Double, Double>>> {
        // Always return a successful result with coordinates (0.0, 0.0)
        return flowOf(Result.success(Pair(0.0, 0.0)))
    }
}