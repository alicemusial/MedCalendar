package com.example.medcalendar.data

import androidx.test.espresso.core.internal.deps.dagger.Module
import androidx.test.espresso.core.internal.deps.dagger.Provides
import com.example.medcalendar.domain.repository.ReminderRepository
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideReminderFirebase(): ReminderFirebase {
        return ReminderFirebase()
    }
    @Provides
    @Singleton
    fun provideReminderRepository(reminderFirebase: ReminderFirebase): ReminderRepository {
        return ReminderRepoImpl(reminderFirebase)
    }

}