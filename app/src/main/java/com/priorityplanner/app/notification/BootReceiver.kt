package com.priorityplanner.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.priorityplanner.app.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = AppDatabase.getDatabase(context)
            val helper = NotificationHelper(context)
            val now = System.currentTimeMillis()

            CoroutineScope(Dispatchers.IO).launch {
                val tasks = database.taskDao().getUpcomingReminders(now)
                tasks.forEach { task ->
                    task.reminderTimeMs?.let { reminderTime ->
                        helper.scheduleTaskReminder(
                            taskId = task.id,
                            title = task.title,
                            priority = task.priority,
                            triggerAtMillis = reminderTime
                        )
                    }
                }
            }
        }
    }
}
