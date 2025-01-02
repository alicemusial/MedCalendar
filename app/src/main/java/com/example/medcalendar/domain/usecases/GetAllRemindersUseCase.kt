package com.example.medcalendar.domain.usecases

import com.example.medcalendar.domain.repository.ReminderRepository
import javax.inject.Inject

class GetAllRemindersUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke() = reminderRepository.getAllReminders()
}