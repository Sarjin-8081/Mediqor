package com.example.mediqorog.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mediqorog.R
import com.example.mediqorog.model.MedicineReminder
import java.util.*

object NotificationHelper {

    private const val CHANNEL_ID = "medicine_reminder_channel"
    private const val CHANNEL_NAME = "Medicine Reminders"
    private const val CHANNEL_DESC = "Notifications for medicine intake reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(
        context: Context,
        reminder: MedicineReminder,
        prescriptionId: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        reminder.times.forEachIndexed { index, time ->
            val (hour, minute) = time.split(":").map { it.toInt() }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                // If time has passed today, schedule for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val intent = Intent(context, MedicineReminderReceiver::class.java).apply {
                putExtra("medicine_name", reminder.medicineName)
                putExtra("dosage", reminder.dosage)
                putExtra("prescription_id", prescriptionId)
            }

            val requestCode = prescriptionId.hashCode() + index
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule repeating alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, // Repeat daily
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, prescriptionId: String, timesCount: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        repeat(timesCount) { index ->
            val intent = Intent(context, MedicineReminderReceiver::class.java)
            val requestCode = prescriptionId.hashCode() + index
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }

    fun showNotification(
        context: Context,
        medicineName: String,
        dosage: String
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
            .setContentTitle("Time to take your medicine")
            .setContentText("$medicineName - $dosage")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(medicineName.hashCode(), notification)
    }
}

class MedicineReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicine_name") ?: return
        val dosage = intent.getStringExtra("dosage") ?: return

        NotificationHelper.showNotification(context, medicineName, dosage)
    }
}