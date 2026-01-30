package com.example.mediqorog.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.mediqorog.utils.ReminderScheduler
import com.example.mediqorog.viewmodel.PrescriptionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver that reschedules all reminders when device boots up
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // In a real app, you would fetch prescriptions from SharedPreferences or Database
            // and reschedule them. For now, this is a placeholder.

            // You could use WorkManager here for more reliable background work
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Load prescriptions from local storage or Firebase
                    // and reschedule reminders
                    // ReminderScheduler.rescheduleAllReminders(context, prescriptions)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}