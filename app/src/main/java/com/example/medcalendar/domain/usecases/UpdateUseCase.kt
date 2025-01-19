package com.example.medcalendar.domain.usecases

import com.example.medcalendar.data.repository.ReminderRepoImpl
import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.data.repository.ReminderRepository
import com.example.medcalendar.presentation.UiState
import javax.inject.Inject

class UpdateUseCase @Inject constructor(private val reminderRepoImpl : ReminderRepoImpl){
    operator fun invoke(reminder: Reminder) = reminderRepoImpl.update(reminder, result = { UiState.Success("Reminder updated successfully")})
}
