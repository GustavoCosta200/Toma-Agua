package com.example.tomagua.data.repository

import com.example.tomagua.data.local.dao.ConsumptionRecordsDao
import com.example.tomagua.data.local.entity.ConsumptionRecords
import com.example.tomagua.domain.repository.ConsumptionRecordsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class ConsumptionRecordsRepositoryImpl @Inject constructor(
    private val consumptionRecordsDao: ConsumptionRecordsDao
) : ConsumptionRecordsRepository {

    override fun watchTotalConsumedInPeriod(start: LocalDateTime, end: LocalDateTime): Flow<Int> =
        consumptionRecordsDao.watchTotalConsumedInPeriod(start, end)

    override fun watchByPeriod(start: LocalDateTime, end: LocalDateTime):
            Flow<List<ConsumptionRecords>> = consumptionRecordsDao.watchByPeriod(start, end)

    override fun watchByConfiguration(configurationId: Long): Flow<List<ConsumptionRecords>> =
        consumptionRecordsDao.watchByConfiguration(configurationId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun watchTotalConsumedTodayByProfile(profileId: Long): Flow<Int> =
        currentDateFlow().flatMapLatest { today ->
            consumptionRecordsDao.watchTotalConsumedTodayByProfile(
                profileId = profileId,
                startOfDay = today.atStartOfDay(),
                endOfDay = today.atTime(LocalTime.MAX)
            )
        }

    override fun watchByProfileAndDate(profileId: Long, date: LocalDate): Flow<List<ConsumptionRecords>> =
        consumptionRecordsDao.watchByProfileAndPeriod(
            profileId = profileId,
            start = date.atStartOfDay(),
            end = date.atTime(LocalTime.MAX)
        )

    override suspend fun insert(consumptionRecords: ConsumptionRecords): Long =
        consumptionRecordsDao.insert(consumptionRecords)

    override suspend fun delete(consumptionRecords: ConsumptionRecords) =
        consumptionRecordsDao.delete(consumptionRecords)

    override suspend fun confirm(id: Long) =
        consumptionRecordsDao.confirm(id)

    /** Emite a data atual agora e de novo a cada virada de dia, enquanto houver coletor. */
    private fun currentDateFlow(): Flow<LocalDate> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(now.toLocalDate())
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
            delay(Duration.between(now, nextMidnight).toMillis())
        }
    }
}