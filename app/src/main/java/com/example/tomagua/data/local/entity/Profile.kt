package com.example.tomagua.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isActive: Boolean = false,
    // O defaultValue precisa bater com o DEFAULT da migration.
    @ColumnInfo(defaultValue = "0") val requirePhoto: Boolean = false
)