package com.droidcon.weatherscope

import android.app.Application
import com.droidcon.weatherscope.injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)

            modules(
                viewModelModule
            )
        }
    }
}