package com.example.tomagua.domain.repository

import com.example.tomagua.data.local.entity.Profile
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para acesso a dados de Perfil.
 *
 * O ViewModel depende APENAS desta interface, nunca do ProfileDao
 * diretamente. Isso permite trocar a implementação (ex: adicionar cache,
 * trocar Room por outra fonte) sem alterar nenhuma linha do ViewModel,
 * e permite criar um FakeProfileRepository em testes sem precisar de banco.
 */
interface ProfileRepository {
    /*Observa todos os perifs*/
    fun showAll(): Flow<List<Profile>>

    /*Observa perfil ativo*/
    fun showActive(): Flow<Profile?>

    // Observa Perfil a partir do Id
    suspend fun getProfileById(profileId: Long): Profile?

    suspend fun insert(profile: Profile): Long

    suspend fun update(profile: Profile)

    suspend fun delete(profile: Profile)

    /* Define o perfil com [id] como o único perfil ativo
    * A Operação é realizada no DAO*/
    suspend fun defineAsActive(id: Long)
}