package com.example.medcalendar.domain.model

data class Reminder(
    var id: String,
    var name: String,
    var dosage: String,
    var time: Long,
    var isTaken: Boolean = false,
    var isRepeating: Boolean = false
)
