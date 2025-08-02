package com.droidcon.weatherscope.di

import android.content.Context
import com.droidcon.weatherscope.data.database.AppDatabase
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastDao
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastLocalSource
import com.droidcon.weatherscope.data.network.services.openweather.WeatherApiService
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
object DataModule {
    @Single
    fun provideDatabase(context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    @Single
    fun provideWeatherDao(database: AppDatabase) =
        database.weatherForecastDao()

    @Single
    fun provideLocalSource(dao: WeatherForecastDao) =
        WeatherForecastLocalSource(dao)

    @Single
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        localSource: WeatherForecastLocalSource
    ) = WeatherRepository(apiService, localSource)
}