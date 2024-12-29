package com.example.medcalendar.data

import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.domain.repository.ReminderRepository

class ReminderRepoImpl(private val reminderFirebase: ReminderFirebase) : ReminderRepository {
    override suspend fun insert(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun update(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllReminders(): List<Reminder> {
        TODO("Not yet implemented")
    }

}