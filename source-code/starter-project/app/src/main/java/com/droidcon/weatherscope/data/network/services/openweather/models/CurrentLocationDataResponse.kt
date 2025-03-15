package com.droidcon.weatherscope.data.network.services.openweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentLocationDataResponse(
    @Json(name = "name")
    val name: String,

    @Json(name = "local_names")
    val localNames: Map<String, String>?, // Using Map for dynamic keys

    @Json(name = "lat")
    val lat: Double,

    @Json(name = "lon")
    val lon: Double,

    @Json(name = "country")
    val country: String? = null,

    @Json(name = "state")
    val state: String?
)
