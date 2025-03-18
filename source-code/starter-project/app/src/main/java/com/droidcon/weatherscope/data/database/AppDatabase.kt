package com.droidcon.weatherscope.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastDao
import com.droidcon.weatherscope.data.database.forecast.WeatherForecastEntity

@Database(entities = [WeatherForecastEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherForecastDao(): WeatherForecastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_scope_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}