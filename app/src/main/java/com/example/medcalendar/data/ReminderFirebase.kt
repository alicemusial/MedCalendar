package com.example.medcalendar.data

import com.example.medcalendar.domain.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore

class ReminderFirebase {
    private val db = FirebaseFirestore.getInstance()
    private val remindersCollection = db.collection("reminders")

    // Dodaj przypomnienie do bazy danych
    fun addReminder(reminder: Reminder, onCompleteListener: (Boolean) -> Unit) {
        remindersCollection.add(reminder)
            .addOnSuccessListener {
                onCompleteListener(true)
            }
            .addOnFailureListener {
                onCompleteListener(false)
            }
    }

    // Pobierz wszystkie przypomnienia z bazy danych
    fun getAllReminders(onCompleteListener: (List<Reminder>) -> Unit) {
        remindersCollection.get()
            .addOnSuccessListener { result ->
                val reminders = mutableListOf<Reminder>()
                for (document in result) {
                    val reminder = document.toObject(Reminder::class.java)
                    reminder.id = document.id
                    reminders.add(reminder)
                }
                onCompleteListener(reminders)
            }
            .addOnFailureListener {
                    onCompleteListener(emptyList())
                }
            }

    // Usuń przypomnienie z bazy danych
    fun deleteReminder(reminderId: String,
                       onCompleteListener: (Boolean) -> Unit) {
        remindersCollection.document(reminderId).delete()
            .addOnSuccessListener {
                onCompleteListener(true)
            }
            .addOnFailureListener {
                onCompleteListener(false)
            }
    }

    // Aktualizuj przypomnienie w bazie danych
    fun updateReminder(reminder: Reminder,
                       onCompleteListener: (Boolean) -> Unit) {
        if (reminder.id.isBlank()) {
            onCompleteListener(false)
            return
        }
        remindersCollection.document(reminder.id).set(reminder)
            .addOnSuccessListener {
                onCompleteListener(true)
            }
            .addOnFailureListener {
                onCompleteListener(false)
            }
    }
}
