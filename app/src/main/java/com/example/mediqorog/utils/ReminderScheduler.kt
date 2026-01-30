package com.example.mediqorog.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mediqorog.model.MedicineReminder
import com.example.mediqorog.view.ReminderReceiver
import java.util.*

object ReminderScheduler {

    fun scheduleReminder(context: Context, reminder: MedicineReminder, prescriptionId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        reminder.times.forEachIndexed { index, time ->
            val (hour, minute) = parseTime(time)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If time has passed today, schedule for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("medicineName", reminder.medicineName)
                putExtra("dosage", reminder.dosage)
                putExtra("prescriptionId", prescriptionId)
            }

            val requestCode = generateRequestCode(prescriptionId, index)
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
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, prescriptionId: String, timesCount: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (index in 0 until timesCount) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val requestCode = generateRequestCode(prescriptionId, index)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun rescheduleAllReminders(context: Context, prescriptions: List<com.example.mediqorog.model.Prescription>) {
        prescriptions.forEach { prescription ->
            prescription.reminders.forEach { reminder ->
                if (reminder.enabled && reminder.endDate.after(Date())) {
                    scheduleReminder(context, reminder, prescription.id)
                }
            }
        }
    }

    private fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts.getOrNull(1)?.toInt() ?: 0
        return Pair(hour, minute)
    }

    private fun generateRequestCode(prescriptionId: String, index: Int): Int {
        return (prescriptionId.hashCode() + index) and Int.MAX_VALUE
    }
}