package com.droidcon.weatherscope.utils

/**
 * Helper extension function to capitalize the first letter of a string
 */
fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1)
    } else {
        this
    }
}