package com.example.tomagua.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString() // HH:mm

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString() // ISO-8601

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let{ LocalDateTime.parse(it) }
}