package com.example.medcalendar.domain.repository

import com.example.medcalendar.domain.model.Reminder

interface ReminderRepository {
    suspend fun insert(reminder: Reminder)

    suspend fun update(reminder: Reminder)

    suspend fun delete(reminder: Reminder)

    suspend fun getAllReminders(): List<Reminder>

}