package com.example.tomagua.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "configurations_reminder",
    foreignKeys = [ForeignKey(
        entity = Profile::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("profileId")]
)

data class ConfigurationReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val hourInterval: Int,
    val startHour: LocalTime,
    val endHour: LocalTime,
    val mlQuantity: Int,
    val soundUri: String?,
    val message: String
)