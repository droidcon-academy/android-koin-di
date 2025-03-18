package com.droidcon.weatherscope.ui.common

sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data class Success<T>(val state: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}
