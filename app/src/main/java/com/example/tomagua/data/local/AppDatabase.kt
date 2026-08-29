package com.example.tomagua.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tomagua.data.local.dao.ConfigurationReminderDao
import com.example.tomagua.data.local.dao.ProfileDao
import com.example.tomagua.data.local.entity.ConfigurationReminder
import com.example.tomagua.data.local.entity.ConsumptionRecords
import com.example.tomagua.data.local.entity.Profile

@Database(
    entities = [Profile::class, ConfigurationReminder::class, ConsumptionRecords::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun configurationReminderDao(): ConfigurationReminderDao
}