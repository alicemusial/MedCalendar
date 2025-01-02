package com.example.medcalendar.domain.usecases

import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateUseCase @Inject constructor(private val reminderRepository : ReminderRepository){
    suspend operator fun invoke(reminder: Reminder) = reminderRepository.update(reminder)
}
