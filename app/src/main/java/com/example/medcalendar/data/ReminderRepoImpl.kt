package com.example.medcalendar.data

import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.domain.repository.ReminderRepository

class ReminderRepoImpl(private val reminderFirebase: ReminderFirebase) : ReminderRepository {
    override suspend fun insert(reminder: Reminder) {
        reminderFirebase.addReminder(reminder) {
            success -> if (!success) {
                throw Exception("Failed to add reminder")
        }
        }
    }

    override suspend fun update(reminder: Reminder) {
        reminderFirebase.updateReminder(reminder){
            success -> if (!success) {
                throw Exception("Failed to update reminder")
            }
        }
    }

    override suspend fun delete(reminder: Reminder) {
        reminderFirebase.updateReminder(reminder){
            success -> if (!success) {
                throw Exception("Failed to delete reminder")
            }
        }
    }

    override suspend fun getAllReminders(): List<Reminder> {
        var resultList: List<Reminder>? = null
        var successFlag = false

        reminderFirebase.getAllReminders { reminders ->
            resultList = reminders
            successFlag = true
        }

        if (!successFlag || resultList == null) {
            throw Exception("Failed to fetch reminders from Firebase.")
        }

        return resultList!!
    }
}