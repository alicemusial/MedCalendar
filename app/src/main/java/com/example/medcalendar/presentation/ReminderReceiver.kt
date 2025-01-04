package com.example.medcalendar.presentation

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.medcalendar.CHANNEL
import com.example.medcalendar.domain.model.Reminder
import com.google.gson.Gson
import com.example.medcalendar.R


const val DONE = "DONE"
const val REJECT = "REJECT"

class ReminderReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val reminderJson = intent.getStringExtra(REMINDER)
        val reminder = Gson().fromJson(reminderJson, Reminder::class.java)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){

                val doneIntent = Intent(context, ReminderReceiver::class.java).apply{
                    putExtra(REMINDER,reminderJson)
                    action = REJECT
                }

                val donePendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminder.time.toInt(),
                    doneIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Medication Reminder")
                    .setContentText(reminder.name.plus("${reminder.name}"))
                    .addAction(R.drawable.check_box, "Done", donePendingIntent)
                    .addAction(R.drawable.close, "Reject", donePendingIntent)
                    .build()
                }
            }
    }
}