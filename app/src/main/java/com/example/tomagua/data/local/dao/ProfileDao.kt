package com.example.tomagua.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tomagua.data.local.entity.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * from profiles ORDER BY name")
    fun showAll(): Flow<List<Profile>>

    @Query("SELECT * from profiles WHERE isActive = 1 LIMIT 1")
    fun showActive(): Flow<Profile?>

    @Query("SELECT * from profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: Long): Profile?

    @Insert
    suspend fun insert(profile: Profile): Long

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)

    @Query("UPDATE profiles set isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE profiles set isActive = 1 WHERE id =:id")
    suspend fun activate(id: Long)

    @Transaction
    suspend fun defineAsActive(id:Long){
        deactivateAll()
        activate(id)
    }
}