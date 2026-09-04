package com.example.tomagua.di

import com.example.tomagua.data.repository.ConfigurationReminderRepositoryImpl
import com.example.tomagua.data.repository.ConsumptionRecordsRepositoryImpl
import com.example.tomagua.data.repository.ProfileRepositoryImpl
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import com.example.tomagua.domain.repository.ConsumptionRecordsRepository
import com.example.tomagua.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton


/**
 * @Binds é usado (em vez de @Provides) porque estamos apenas dizendo ao Hilt
 * "quando pedirem a interface X, entregue a implementação Y" — não há lógica
 * de construção manual envolvida, o Hilt já sabe construir XRepositoryImpl
 * sozinho porque seu construtor tem @Inject e o Dao correspondente já é
 * fornecido pelo AppDatabase.
 *
 * A classe precisa ser 'abstract' porque métodos @Binds não têm corpo:
 * eles são apenas uma "declaração de mapeamento" para o Dagger/Hilt gerar
 * o código de fato em tempo de compilação.
 */
@Module
@InstallIn
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindConfigurationReminderRepository(
        impl: ConfigurationReminderRepositoryImpl
    ): ConfigurationReminderRepository

    @Binds
    @Singleton
    abstract fun bindConsumptionRecordsRepository(
        impl: ConsumptionRecordsRepositoryImpl
    ): ConsumptionRecordsRepository
}