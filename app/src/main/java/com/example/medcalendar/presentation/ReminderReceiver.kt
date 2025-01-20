package com.example.medcalendar.presentation

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.medcalendar.CHANNEL
import com.example.medcalendar.domain.model.Reminder
import com.google.gson.Gson
import com.example.medcalendar.R
import com.example.medcalendar.domain.usecases.UpdateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


const val DONE = "DONE"
const val REJECT = "REJECT"

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver(){

    @Inject
    lateinit var updateUseCase: UpdateUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val reminderJson = intent.getStringExtra(REMINDER)
        val reminder = Gson().fromJson(reminderJson, Reminder::class.java)

        Log.d("ReminderReceiver", "Received reminder: $reminder")

        val doneIntent = Intent(context, ReminderReceiver::class.java).apply{
            putExtra(REMINDER,reminderJson)
            action = DONE
        }

        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.time.toInt(),
            doneIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val rejectIntent = Intent(context, ReminderReceiver::class.java).apply{
            putExtra(REMINDER,reminderJson)
            action = REJECT
        }

        val rejectPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.time.toInt(),
            rejectIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        when(intent.action){
            DONE -> {
                runBlocking { updateUseCase.invoke(reminder.copy(isTaken = true)) }
                cancelAlarm(context, reminder)
                Log.d("ReminderReceiver", "Reminder marked as taken: $reminder")
            }
            REJECT -> {
                runBlocking { updateUseCase.invoke(reminder.copy(isTaken = false)) }
                cancelAlarm(context, reminder)
                Log.d("ReminderReceiver", "Received reminder: $reminder")
            }
            else -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if(ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){


                        val notification = NotificationCompat.Builder(context, CHANNEL)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Medication Reminder")
                            .setContentText(reminder.name.plus("${reminder.dosage}"))
                            .addAction(R.drawable.check_box, "Done", donePendingIntent)
                            .addAction(R.drawable.close, "Reject", rejectPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .build()

                        NotificationManagerCompat.from(context).notify(reminder.time.toInt(), notification)
                    }else{
                        requestNotificationPermission(context)
                    }
                } else {
                    Log.d("ReminderReceiver", "Received reminder: $reminder")
                    val notification = NotificationCompat.Builder(context, CHANNEL)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Medication Reminder")
                        .setContentText(reminder.name.plus("${reminder.dosage}"))
                        .addAction(R.drawable.check_box, "Done", donePendingIntent)
                        .addAction(R.drawable.close, "Reject", rejectPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build()

                    NotificationManagerCompat.from(context).notify(reminder.time.toInt(), notification)
                }

            }
        }


    }
}

fun requestNotificationPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        permissionIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(permissionIntent)
    }
}