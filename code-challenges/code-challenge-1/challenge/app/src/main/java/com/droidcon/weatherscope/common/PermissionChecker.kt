package com.droidcon.weatherscope.common

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Interface for checking permissions
 */
interface PermissionChecker {
    fun hasPermission(permission: String): Boolean
}

/**
 * Android implementation of PermissionChecker
 */
class AndroidPermissionChecker(private val context: Context) : PermissionChecker {
    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}