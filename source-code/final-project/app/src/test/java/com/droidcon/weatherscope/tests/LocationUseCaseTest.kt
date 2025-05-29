package com.droidcon.weatherscope.tests

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.PermissionChecker
import com.droidcon.weatherscope.common.StringResourcesProvider
import com.droidcon.weatherscope.di.UtilsModule
import io.mockk.mockkClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declare
import org.koin.test.mock.declareMock
import kotlin.test.assertNotNull

class LocationUseCaseTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Before
    fun setup() {
        startKoin {
            modules(UtilsModule.module)
        }
        declare<GetCurrentLocationUseCase> {
            // Provide a fake implementation for testing
            FakeGetCurrentLocationUseCase()
        }
        declareMock<DataStore<Preferences>>()
        declareMock<AppPreferences>()
        declareMock<PermissionChecker>()
        declareMock<StringResourcesProvider>()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testFakeLocationUseCase() {
        val useCase: GetCurrentLocationUseCase by inject()
        assertNotNull(useCase)
    }
}

class FakeGetCurrentLocationUseCase : GetCurrentLocationUseCase {
    override fun execute(): Flow<Result<Pair<Double, Double>>> {
        return flowOf(Result.success(Pair(0.0, 0.0)))
    }
}