package com.droidcon.weatherscope.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.droidcon.weatherscope.common.AndroidPermissionChecker
import com.droidcon.weatherscope.common.AppPreferences
import com.droidcon.weatherscope.common.AppStringResourcesProvider
import com.droidcon.weatherscope.common.GetCurrentLocationUseCase
import com.droidcon.weatherscope.common.GetCurrentLocationUseCaseImpl
import com.droidcon.weatherscope.common.PermissionChecker
import com.droidcon.weatherscope.common.StringResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.module

private const val APP_PREFERENCES_NAME = "app_settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCES_NAME)

@Module
object UtilsModule {
    @Single
    fun provideDataStore(context: Context): DataStore<Preferences> =
        context.dataStore

    @Single
    fun provideAppPreferences(dataStore: DataStore<Preferences>) =
        AppPreferences(dataStore)

    @Single
    fun providePermissionChecker(context: Context): PermissionChecker =
        AndroidPermissionChecker(context)

    @Single
    fun provideLocationUseCase(
        checker: PermissionChecker,
        context: Context
    ): GetCurrentLocationUseCase = GetCurrentLocationUseCaseImpl(context, checker)

    @Single
    fun provideStringResources(context: Context): StringResourcesProvider =
        AppStringResourcesProvider(context.resources)
}