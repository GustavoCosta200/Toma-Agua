package com.example.tomagua.data.repository

import com.example.tomagua.data.local.dao.ConsumptionRecordsDao
import com.example.tomagua.data.local.entity.ConsumptionRecords
import com.example.tomagua.domain.repository.ConsumptionRecordsRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class ConsumptionRecordsRepositoryImpl @Inject constructor(
    private val consumptionRecordsDao: ConsumptionRecordsDao
) : ConsumptionRecordsRepository{

    override fun watchTotalConsumedInPeriod(start: LocalDateTime, end: LocalDateTime): Flow<Int> =
        consumptionRecordsDao.watchTotalConsumedInPeriod(start, end)

    override fun watchByPeriod(start: LocalDateTime, end: LocalDateTime):
            Flow<List<ConsumptionRecords>> = consumptionRecordsDao.watchByPeriod(start, end)

    override fun watchByConfiguration(configurationId: Long): Flow<List<ConsumptionRecords>> =
        consumptionRecordsDao.watchByConfiguration(configurationId)

    override suspend fun insert(consumptionRecords: ConsumptionRecords): Long =
        consumptionRecordsDao.insert(consumptionRecords)

    override suspend fun delete(consumptionRecords: ConsumptionRecords) =
        consumptionRecordsDao.delete(consumptionRecords)

    override suspend fun confirm(id: Long) =
        consumptionRecordsDao.confirm(id)
}