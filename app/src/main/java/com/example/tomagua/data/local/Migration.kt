package com.example.tomagua.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// V1 -> V2 : Foto de Registro no consumo e modo exigir foto no perfil
val MIGRATION_1_2 = object: Migration(1, 2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE consumption_records ADD COLUMN photoPath TEXT")
        db.execSQL("ALTER TABLE profiles ADD COLUMN requirePhoto INTEGER NOT NULL DEFAULT 0")
    }
}