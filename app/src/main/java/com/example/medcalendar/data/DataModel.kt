package com.example.medcalendar.data

import androidx.test.espresso.core.internal.deps.dagger.Module
import androidx.test.espresso.core.internal.deps.dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModel {
    @Provides
    @Singleton
    fun provideReminderFirebase(): ReminderFirebase {
        return ReminderFirebase()
    }

}