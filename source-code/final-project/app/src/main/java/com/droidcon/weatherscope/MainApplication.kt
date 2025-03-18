package com.droidcon.weatherscope

import android.app.Application
import com.droidcon.weatherscope.di.domainModule
import com.droidcon.weatherscope.di.networkModule
import com.droidcon.weatherscope.di.dataModule
import com.droidcon.weatherscope.di.utilsModule
import com.droidcon.weatherscope.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)

            modules(
                viewModelModule,
                utilsModule,
                networkModule,
                domainModule,
                dataModule
            )
        }
    }
}