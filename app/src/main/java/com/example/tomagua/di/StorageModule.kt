package com.example.tomagua.di

import com.example.tomagua.data.storage.FilePhotoStorage
import com.example.tomagua.domain.storage.PhotoStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule{

    @Binds
    abstract fun bindPhotoStorage(impl: FilePhotoStorage): PhotoStorage
}