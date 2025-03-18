package com.droidcon.weatherscope.di

import com.droidcon.weatherscope.data.network.buildRetrofit
import com.droidcon.weatherscope.data.network.services.openweather.GeocodingApiService
import com.droidcon.weatherscope.data.network.services.openweather.OPEN_WEATHER_API
import com.droidcon.weatherscope.data.network.services.openweather.OPEN_WEATHER_GEOCODING_API
import com.droidcon.weatherscope.data.network.services.openweather.WeatherApiService
import com.droidcon.weatherscope.common.AppPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single {
        ApiKeyInterceptor(get())
    }

    // OkHttpClient
    factory {
        OkHttpClient.Builder()
            .addInterceptor(get<ApiKeyInterceptor>())
            .build()
    }

    // Weather API Retrofit instance
    single(named("weatherRetrofit")) {
        buildRetrofit(get(), baseUrl = OPEN_WEATHER_API)
    }

    // Geocoding API Retrofit instance
    single(named("geocodingRetrofit")) {
        buildRetrofit(get(), baseUrl = OPEN_WEATHER_GEOCODING_API)
    }

    // Weather API service
    single {
        get<Retrofit>(named("weatherRetrofit")).create(WeatherApiService::class.java)
    }

    // Geocoding API service
    single {
        get<Retrofit>(named("geocodingRetrofit")).create(GeocodingApiService::class.java)
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