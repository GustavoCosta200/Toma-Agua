package com.example.tomagua.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tomagua.data.local.entity.ConfigurationReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigurationReminderDao {

    @Query("SELECT * FROM configurations_reminder WHERE profileId = :profileId")
    fun watchByProfile(profileId: Long): Flow<List<ConfigurationReminder>>

    @Query("SELECT * FROM configurations_reminder WHERE id = :id")
    suspend fun findById(id: Long): ConfigurationReminder?

    @Query("SELECT COALESCE(SUM(mlQuantity), 0) FROM configurations_reminder WHERE profileId = :profileId")
    fun watchDailyGoal(profileId: Long): Flow<Int>

    @Insert
    suspend fun insert(configurationReminder: ConfigurationReminder): Long

    @Delete
    suspend fun delete(configurationReminder: ConfigurationReminder)

    @Update
    suspend fun update(configurationReminder: ConfigurationReminder)
}