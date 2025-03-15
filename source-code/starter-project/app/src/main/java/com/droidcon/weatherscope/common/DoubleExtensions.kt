package com.droidcon.weatherscope.common

fun Double.toFahrenheit(): Double {
    return (this * 1.8) + 32
}