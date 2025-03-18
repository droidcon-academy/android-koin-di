package com.droidcon.weatherscope.common

import android.content.res.Resources

interface StringResourcesProvider {
    fun getString(stringRes: Int): String

    fun getString(stringRes: Int, vararg formatArgs: Any): String
}

class AppStringResourcesProvider(private val resources: Resources) : StringResourcesProvider {
    override fun getString(stringRes: Int) = resources.getString(stringRes)

    override fun getString(stringRes: Int, vararg formatArgs: Any) =
        resources.getString(stringRes, *formatArgs)
}