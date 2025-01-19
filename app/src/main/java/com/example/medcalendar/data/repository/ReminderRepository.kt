package com.example.medcalendar.data.repository

import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.presentation.UiState

interface ReminderRepository {
    fun insert(reminder: Reminder, result: (UiState<String>) -> Unit)
    fun update(reminder: Reminder, result: (UiState<String>) -> Unit)
    fun delete(reminder: Reminder, result: (UiState<String>) -> Unit)
    fun getAllReminders(result: (UiState<List<Reminder>>) -> Unit)

}