package com.droidcon.weatherscope.injection

import com.droidcon.weatherscope.data.database.AppDatabase
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastLocalSource
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().weatherForecastDao() }
    single { WeatherForecastLocalSource(get()) }
    single { WeatherRepository(get(), get()) }
}