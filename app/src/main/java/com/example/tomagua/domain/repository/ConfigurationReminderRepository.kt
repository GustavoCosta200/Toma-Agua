package com.example.tomagua.domain.repository

import com.example.tomagua.data.local.entity.ConfigurationReminder
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para acesso a dados de Configuração de Lembrete.
 */
interface ConfigurationReminderRepository {

    /* Observa todas as configurações associadas por um perfil*/
    fun watchByProfile(profileId: Long): Flow<List<ConfigurationReminder>>

    suspend fun findById(id: Long): ConfigurationReminder?

    fun watchDailyGoal(profileId: Long): Flow<Int>

    /** Leitura única de todas as configurações (usada para reagendar alarmes após boot). */
    suspend fun findAll(): List<ConfigurationReminder>

    suspend fun insert(configurationReminder: ConfigurationReminder): Long

    suspend fun update(configurationReminder: ConfigurationReminder)

    suspend fun delete(configurationReminder: ConfigurationReminder)
}