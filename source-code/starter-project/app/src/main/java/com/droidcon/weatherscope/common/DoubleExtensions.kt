package com.droidcon.weatherscope.common

fun Double.toFaranheit(): Double {
    return (this * 1.8) + 32
}