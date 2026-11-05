package com.priorityplanner.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.priorityplanner.app.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)
        val taskId = intent.getLongExtra(NotificationHelper.EXTRA_TASK_ID, -1L)

        when (intent.action) {
            NotificationHelper.ACTION_TRIGGER_REMINDER -> {
                if (taskId != -1L) {
                    val title = intent.getStringExtra(NotificationHelper.EXTRA_TASK_TITLE) ?: "Task Reminder"
                    val priority = intent.getIntExtra(NotificationHelper.EXTRA_TASK_PRIORITY, 1)
                    helper.showTaskNotification(taskId, title, priority)
                }
            }
            NotificationHelper.ACTION_MARK_DONE -> {
                if (taskId != -1L) {
                    helper.cancelNotification(taskId)
                    val database = AppDatabase.getDatabase(context)
                    CoroutineScope(Dispatchers.IO).launch {
                        database.taskDao().updateTaskCompletion(
                            taskId = taskId,
                            isCompleted = true,
                            completedAt = System.currentTimeMillis()
                        )
                    }
                }
            }
        }
    }
}
