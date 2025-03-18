package com.droidcon.weatherscope

import com.droidcon.weatherscope.di.dataModule
import com.droidcon.weatherscope.di.domainModule
import com.droidcon.weatherscope.di.networkModule
import com.droidcon.weatherscope.di.utilsModule
import com.droidcon.weatherscope.di.viewModelModule
import org.junit.Test

import org.junit.Assert.*
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
//        viewModelModule.verify()
//        utilsModule.verify()
//        networkModule.verify()
//        domainModule.verify()
//        dataModule.verify()
//    }
}