package com.example.tomagua.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tomagua.data.local.entity.ConsumptionRecords
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.LocalTime

@Dao
interface ConsumptionRecordsDao {

    @Query("SELECT * FROM consumption_records WHERE configurationId = :configurationId " +
            "ORDER BY dateTime desc")
    fun watchByConfiguration(configurationId: Long): Flow<List<ConsumptionRecords>>

    @Query("""
        SELECT * FROM consumption_records
        WHERE dateTime BETWEEN :start AND :end
        ORDER BY dateTime DESC
    """)
    fun watchByPeriod(start: LocalDateTime, end: LocalDateTime): Flow<List<ConsumptionRecords>>

    @Query("""
        SELECT COALESCE(SUM(mlQuantity), 0) FROM consumption_records
        WHERE confirmed = 1 AND dateTime BETWEEN :start AND :end
    """)
    fun watchTotalConsumedInPeriod(start: LocalDateTime, end: LocalDateTime): Flow<Int>

    @Query("""
    SELECT COALESCE(SUM(cr.mlQuantity), 0)
    FROM consumption_records cr
    INNER JOIN configurations_reminder cfg ON cr.configurationId = cfg.id
    WHERE cfg.profileId = :profileId
    AND cr.confirmed = 1
    AND cr.dateTime BETWEEN :startOfDay AND :endOfDay
""")
    fun watchTotalConsumedTodayByProfile(
        profileId: Long,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<Int>

    @Query("""
    SELECT cr.* FROM consumption_records cr
    INNER JOIN configurations_reminder cfg ON cr.configurationId = cfg.id
    WHERE cfg.profileId = :profileId
    AND cr.dateTime BETWEEN :start AND :end
    ORDER BY cr.dateTime DESC
""")
    fun watchByProfileAndPeriod(
        profileId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<ConsumptionRecords>>

    @Insert
    suspend fun insert(consumptionRecords: ConsumptionRecords): Long

    @Delete
    suspend fun delete(consumptionRecords: ConsumptionRecords)

    @Query("UPDATE consumption_records SET confirmed = 1 where id = :id")
    suspend fun confirm(id: Long)
}

