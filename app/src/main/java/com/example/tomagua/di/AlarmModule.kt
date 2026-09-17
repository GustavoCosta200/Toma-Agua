package com.example.tomagua.di

import android.app.AlarmManager
import android.content.Context
import com.example.tomagua.data.alarm.AlarmManagerReminderScheduler
import com.example.tomagua.domain.schedule.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmBindsModule{
    @Binds
    abstract fun bindReminderScheduler(impl: AlarmManagerReminderScheduler): ReminderScheduler
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmProvidesModule{
    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}