// ========== ReminderReceiver.kt ==========
package com.example.mediqorog.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mediqorog.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicineName") ?: "Your medicine"
        val dosage = intent.getStringExtra("dosage") ?: ""

        createNotificationChannel(context)
        showNotification(context, medicineName, dosage)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "medicine_reminder",
            "Medicine Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for medicine reminders"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, medicineName: String, dosage: String) {
        // Check for POST_NOTIFICATIONS permission (Android 13+)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, skip notification
            return
        }

        try {
            val notification = NotificationCompat.Builder(context, "medicine_reminder")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take $medicineName - $dosage")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            // Safe to call notify() here - permission already checked above
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        } catch (e: SecurityException) {
            // Handle any security exceptions gracefully
            e.printStackTrace()
        }
    }
}