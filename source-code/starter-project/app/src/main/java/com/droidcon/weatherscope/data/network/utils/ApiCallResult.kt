package com.droidcon.weatherscope.data.network.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

sealed class ApiCallResult<out T> {
    data object Loading : ApiCallResult<Nothing>()
    data class Success<out T>(val response: T) : ApiCallResult<T>()
    data class Error(val error: Throwable? = null) : ApiCallResult<Nothing>()
}

/**
 * A generic function that wraps a suspend [apiCall] into a Flow emitting ApiCallResult.
 *
 * @param apiCall A suspend lambda function that returns a value of type T.
 * @return A Flow emitting Loading, then Success (or Error if exception occurs).
 */
inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Flow<ApiCallResult<T>> = flow {
    emit(ApiCallResult.Loading)
    try {
        val result = apiCall()
        emit(ApiCallResult.Success(result))
    } catch (e: Exception) {
        emit(ApiCallResult.Error(e))
    }
}.flowOn(Dispatchers.IO)