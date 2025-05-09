package com.droidcon.weatherscope.ui.common

data class TextFieldState(val value: String = "",
                                val errorMessage: String? = null,
                                val isError: Boolean = false)