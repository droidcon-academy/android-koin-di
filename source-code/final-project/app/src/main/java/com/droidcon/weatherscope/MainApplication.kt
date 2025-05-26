package com.droidcon.weatherscope

import android.app.Application
import com.droidcon.weatherscope.di.DataModule
import com.droidcon.weatherscope.di.DomainModule
import com.droidcon.weatherscope.di.NetworkModule
import com.droidcon.weatherscope.di.UtilsModule
import com.droidcon.weatherscope.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)

            modules(
                ViewModelModule.module,
                UtilsModule.module,
                NetworkModule.module,
                DomainModule.module,
                DataModule.module
            )
        }
    }
}