package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.data.network.buildRetrofit
import com.droidcon.weatherscope.data.network.services.openweather.GeocodingApiService
import com.droidcon.weatherscope.data.network.services.openweather.OPEN_WEATHER_API
import com.droidcon.weatherscope.data.network.services.openweather.OPEN_WEATHER_GEOCODING_API
import com.droidcon.weatherscope.data.network.services.openweather.WeatherApiService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.Retrofit

@Module
object NetworkModule {

    @Single
    fun provideApiInterceptor(prefs: AppPreferences): ApiKeyInterceptor =
        ApiKeyInterceptor(prefs)

    @Factory
    fun provideOkHttpClient(interceptor: ApiKeyInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @Single
    @Named("weatherRetrofit")
    fun provideWeatherRetrofit(client: OkHttpClient): Retrofit =
        buildRetrofit(client, OPEN_WEATHER_API)

    @Single
    @Named("geocodingRetrofit")
    fun provideGeocodingRetrofit(client: OkHttpClient): Retrofit =
        buildRetrofit(client, OPEN_WEATHER_GEOCODING_API)

    @Single
    fun provideWeatherApiService(
        @Named("weatherRetrofit") retrofit: Retrofit
    ): WeatherApiService = retrofit.create(WeatherApiService::class.java)

    @Single
    fun provideGeocodingService(
        @Named("geocodingRetrofit") retrofit: Retrofit
    ): GeocodingApiService = retrofit.create(GeocodingApiService::class.java)
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