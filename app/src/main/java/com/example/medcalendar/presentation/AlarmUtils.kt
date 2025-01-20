package com.example.medcalendar.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import com.example.medcalendar.domain.model.Reminder

const val REMINDER = "REMINDER"

@RequiresApi(Build.VERSION_CODES.S)
fun setUpAlarm(context: Context, reminder: Reminder){
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.time.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        // Wyświetl interfejs umożliwiający użytkownikowi włączenie tego uprawnienia
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(context, intent, null)
    }
    try{
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.time, pendingIntent)
    }catch (e:SecurityException){
        Log.d("setUpAlarm", "setUpAlarm fail", e)
    }
}

fun cancelAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.time.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.cancel(pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

fun setUpPeriodicAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.time.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        val interval = 2L * 60L * 1000L
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            reminder.time,
            interval,
            pendingIntent
        )
    } catch (e: SecurityException) {
        Log.d("setUpAlarm", "setUpAlarm fail", e)
    }
}