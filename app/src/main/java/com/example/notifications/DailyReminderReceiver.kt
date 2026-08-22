package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("DailyReminderReceiver", "Received broadcast action: $action")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val config = db.notificationReminderConfigDao().getConfigSync()
                val isEnabled = config?.isEnabled ?: true

                if (action == Intent.ACTION_BOOT_COMPLETED) {
                    // Reschedule alarm on system boot
                    val hour = config?.reminderHour ?: 9
                    val minute = config?.reminderMinute ?: 0
                    NotificationReminderManager.scheduleDailyReminder(
                        context = context,
                        hour = hour,
                        minute = minute,
                        enabled = isEnabled
                    )
                    Log.d("DailyReminderReceiver", "Rescheduled daily reminder after boot")
                    return@launch
                }

                if (action == NotificationReminderManager.ACTION_DAILY_REMINDER) {
                    if (isEnabled) {
                        val applications = db.jobApplicationDao().getAllApplications().firstOrNull() ?: emptyList()
                        val totalApplied = applications.count { it.status.equals("APPLIED", true) }
                        val inInterviews = applications.count { it.status.equals("INTERVIEW", true) }

                        val customTitle = config?.customTitle?.ifBlank { null } ?: "🎯 Daily Job Tracker Digest"
                        val customMessage = config?.customMessage?.ifBlank { null }
                            ?: "Take a moment to check your job dashboard for new recruiter responses & updates!"

                        NotificationReminderManager.showReminderNotification(
                            context = context,
                            title = customTitle,
                            message = customMessage,
                            totalApplied = totalApplied,
                            inInterviews = inInterviews,
                            notificationId = NotificationReminderManager.NOTIFICATION_ID_DAILY
                        )

                        // Update last notified time in database
                        db.notificationReminderConfigDao().updateLastNotified(
                            timestamp = System.currentTimeMillis(),
                            message = "Sent daily digest ($inInterviews interviews, $totalApplied applied)"
                        )

                        // Reschedule for next day
                        val hour = config?.reminderHour ?: 9
                        val minute = config?.reminderMinute ?: 0
                        NotificationReminderManager.scheduleDailyReminder(
                            context = context,
                            hour = hour,
                            minute = minute,
                            enabled = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DailyReminderReceiver", "Error processing daily reminder", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
