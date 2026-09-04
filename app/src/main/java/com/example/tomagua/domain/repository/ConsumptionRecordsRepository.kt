package com.example.tomagua.domain.repository

import com.example.tomagua.data.local.entity.ConsumptionRecords
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Contrato para acesso a dados de Registro de Consumo.
 */
interface ConsumptionRecordsRepository {

    /** Observa os registros associados a uma configuração específica. */
    fun watchByConfiguration(configurationId: Long): Flow<List<ConsumptionRecords>>

    /** Observa os registros dentro de um período (ex: para o histórico do dia). */
    fun watchByPeriod(start: LocalDateTime, end: LocalDateTime): Flow<List<ConsumptionRecords>>

    /** Observa o total (em ml) confirmado como consumido dentro de um período. */
    fun watchTotalConsumedInPeriod(start: LocalDateTime, end: LocalDateTime): Flow<Int>

    suspend fun insert(consumptionRecords: ConsumptionRecords): Long

    suspend fun delete(consumptionRecords: ConsumptionRecords)

    /** Marca um registro como efetivamente consumido pelo usuário. */
    suspend fun confirm(id: Long)
}
