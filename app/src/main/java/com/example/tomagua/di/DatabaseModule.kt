package com.example.tomagua.di

import android.content.Context
import androidx.room.Room
import com.example.tomagua.data.local.AppDatabase
import com.example.tomagua.data.local.dao.ConfigurationReminderDao
import com.example.tomagua.data.local.dao.ConsumptionRecordsDao
import com.example.tomagua.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "toma_agua_database"
        ).build()
    }

    @Provides
    fun provideProfileDao(database: AppDatabase): ProfileDao{
        return database.profileDao()
    }

    @Provides
    fun provideConfigurationReminderDao(database: AppDatabase): ConfigurationReminderDao {
        return database.configurationReminderDao()
    }

    @Provides
    fun provideConsumptionRecordsDao(database: AppDatabase): ConsumptionRecordsDao{
        return database.consumptionRecordsDao()
    }
}