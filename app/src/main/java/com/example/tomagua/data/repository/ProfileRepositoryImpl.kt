package com.example.tomagua.data.repository

import com.example.tomagua.data.local.dao.ProfileDao
import com.example.tomagua.data.local.entity.Profile
import com.example.tomagua.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementação concreta de ProfileRepository, apoiada no Room via ProfileDao.
 *
 * Note que esta classe NÃO tem @Inject constructor sozinha resolvendo tudo:
 * o Hilt sabe como fornecer um ProfileDao porque o próprio AppDatabase
 * expõe esse DAO como binding.
 */
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
): ProfileRepository {

    override fun showAll(): Flow<List<Profile>> = profileDao.showAll()

    override fun showActive(): Flow<Profile?> = profileDao.showActive()

    override suspend fun getProfileById(profileId: Long): Profile? =
        profileDao.getProfileById(profileId)

    override suspend fun insert(profile: Profile): Long = profileDao.insert(profile)

    override suspend fun update(profile: Profile) = profileDao.update(profile)

    override suspend fun delete(profile: Profile) = profileDao.delete(profile)

    override suspend fun defineAsActive(id: Long) = profileDao.defineAsActive(id)

}