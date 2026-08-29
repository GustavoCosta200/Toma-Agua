package com.example.tomagua.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "consumption_records",
    foreignKeys = [ForeignKey(
        entity = ConfigurationReminder::class,
        parentColumns = ["id"],
        childColumns = ["configurationId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("configurationId")]
)

data class ConsumptionRecords(
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    val configurationId: Long,
    val dateTime: LocalDateTime,
    val mlQuantity: Int,
    val confirmed: Boolean = false
)