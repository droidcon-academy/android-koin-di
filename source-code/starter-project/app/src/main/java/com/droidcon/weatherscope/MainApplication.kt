package com.droidcon.weatherscope

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.data.database.AppDatabase
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastLocalSource
import com.droidcon.weatherscope.data.network.services.openweather.OPEN_WEATHER_API
import com.droidcon.weatherscope.data.network.services.openweather.WeatherApiService
import com.droidcon.weatherscope.data.repositories.WeatherRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainApplication : Application() {

    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

    // App components
    val appPreferences by lazy { AppPreferences(dataStore) }

    // Network components
    val apiKeyInterceptor by lazy { ApiKeyInterceptor(appPreferences) }
    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }
    val weatherRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_API)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val weatherApiService by lazy { weatherRetrofit.create(WeatherApiService::class.java) }

    // Database components
    val appDatabase by lazy { AppDatabase.getDatabase(this) }
    val weatherForecastDao by lazy { appDatabase.weatherForecastDao() }
    val weatherForecastLocalSource by lazy { WeatherForecastLocalSource(weatherForecastDao) }

    // Repository
    val weatherRepository by lazy {
        WeatherRepository(
            apiService = weatherApiService,
            localSource = weatherForecastLocalSource
        )
    }
}

class ApiKeyInterceptor(private val appPreferences: AppPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val apiKey = runBlocking { appPreferences.apiKey.firstOrNull() }
            ?: return Response.Builder()
                .request(original)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("API key missing, set a valid api key in settings screen.")
                .body("".toResponseBody(null))
                .build()

        val url = originalUrl.newBuilder()
            .addQueryParameter("appid", apiKey)
            .build()

        val request = original.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}
