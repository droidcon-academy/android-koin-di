package com.droidcon.weatherscope

import com.droidcon.weatherscope.di.DataModule
import com.droidcon.weatherscope.di.DomainModule
import com.droidcon.weatherscope.di.NetworkModule
import com.droidcon.weatherscope.di.UtilsModule
import com.droidcon.weatherscope.di.ViewModelModule
import org.junit.Test

import org.junit.Assert.*
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest: KoinTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

//    @Test
//    fun checkAllModules() {
//        ViewModelModule.module.verify()
//        UtilsModule.module.verify()
//        NetworkModule.module.verify()
//        DomainModule.module.verify()
//        DataModule.module.verify()
//    }
}