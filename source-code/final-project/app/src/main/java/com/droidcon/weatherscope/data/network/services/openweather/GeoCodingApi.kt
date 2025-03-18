package com.droidcon.weatherscope.data.network.services.openweather

import com.droidcon.weatherscope.data.network.services.openweather.models.CurrentLocationDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    /**
     * Get geographic coordinates for a city name
     * @param cityName The name of the city (can include state and country)
     * @param limit Maximum number of results to return (default: 1)
     * @param apiKey OpenWeatherMap API key
     * @return List of location data
     */
    @GET("direct")
    suspend fun getLocationByName(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1, // Default to 1 to get only the first result
        @Query("appid") apiKey: String
    ): List<CurrentLocationDataResponse>
}