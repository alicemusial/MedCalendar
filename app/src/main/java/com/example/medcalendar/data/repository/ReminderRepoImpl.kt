package com.example.medcalendar.data.repository

import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.presentation.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.example.medcalendar.data.FirestoreTables
import javax.inject.Inject

class ReminderRepoImpl @Inject constructor(val database: FirebaseFirestore) :
    ReminderRepository {
    override fun insert(reminder: Reminder, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreTables.REMINDER).document()
        reminder.id = document.id
        document
            .set(reminder)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Reminder added successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun update(reminder: Reminder, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreTables.REMINDER).document(reminder.id)
        document
            .set(reminder)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Reminder updated successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun delete(reminder: Reminder, result: (UiState<String>) -> Unit) {
        val document = database.collection(FirestoreTables.REMINDER).document(reminder.id)
        document
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success("Reminder deleted successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override fun getAllReminders(result: (UiState<List<Reminder>>) -> Unit) {
        database.collection(FirestoreTables.REMINDER)
            .get()
            .addOnSuccessListener {
                val reminders = arrayListOf<Reminder>()
                for (document in it) {
                    val reminder = document.toObject(Reminder::class.java)
                    reminders.add(reminder)
                }
                result.invoke(UiState.Success(reminders))
            }
            .addOnFailureListener{
                result.invoke(UiState.Failure(it.localizedMessage))
            }

    }
}