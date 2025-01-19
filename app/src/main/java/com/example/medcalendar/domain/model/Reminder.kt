package com.example.medcalendar.domain.model

data class Reminder(
    var name: String = "",
    var dosage: String = "",
    var time: Long = 0L,
    var isTaken: Boolean = false,
    var isRepeating: Boolean = false,
    var id: String = ""
){

}
