package com.example.tomagua.data.repository

import com.example.tomagua.data.local.dao.ConfigurationReminderDao
import com.example.tomagua.data.local.entity.ConfigurationReminder
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfigurationReminderRepositoryImpl @Inject constructor(
    private val configurationReminderDao: ConfigurationReminderDao
) : ConfigurationReminderRepository{

    override fun watchByProfile(profileId: Long): Flow<List<ConfigurationReminder>> =
        configurationReminderDao.watchByProfile(profileId)

    override suspend fun findById(id: Long): ConfigurationReminder? =
        configurationReminderDao.findById(id)

    override fun watchDailyGoal(profileId: Long): Flow<Int> =
        configurationReminderDao.watchDailyGoal(profileId)

    override suspend fun insert(configurationReminder: ConfigurationReminder): Long =
        configurationReminderDao.insert(configurationReminder)

    override suspend fun update(configurationReminder: ConfigurationReminder) =
        configurationReminderDao.update(configurationReminder)

    override suspend fun delete(configurationReminder: ConfigurationReminder) =
        configurationReminderDao.delete(configurationReminder)

}