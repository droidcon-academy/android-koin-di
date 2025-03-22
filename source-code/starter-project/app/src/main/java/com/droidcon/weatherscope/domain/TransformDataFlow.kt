package com.droidcon.weatherscope.domain

import com.droidcon.weatherscope.data.network.utils.ApiCallResult
import com.droidcon.weatherscope.ui.common.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

/**
 * Generic extension function for converting a Flow<ApiCallResult<T>> to a Flow<DataState<R>>
 *
 * @param mapper A function that converts an object of type T to R
 */
fun <T, R> Flow<ApiCallResult<T>>.toDomainStateFlow(
    mapper: (T) -> R
): Flow<DataState<R>> =
    map { apiResult ->
        when (apiResult) {
            is ApiCallResult.Loading -> DataState.Loading
            is ApiCallResult.Success<T> -> {
                try {
                    // Convert API response to domain model using the provided mapping function
                    val domainModel = mapper(apiResult.response)
                    DataState.Success(domainModel)
                } catch (e: Exception) {
                    // Handle mapping errors
                    DataState.Error("Failed to process data: ${e.message}")
                }
            }

            is ApiCallResult.Error -> {
                // Convert API error to domain error with a custom message
                val errorMessage = when (apiResult.error) {
                    is IOException ->
                        "Network error. Please check your connection."

                    is HttpException ->
                        "Server error (${apiResult.error.code()}): ${apiResult.error.message()}"

                    else ->
                        "An unexpected error occurred: ${apiResult.error?.message}"
                }
                DataState.Error(errorMessage)
            }
        }
    }