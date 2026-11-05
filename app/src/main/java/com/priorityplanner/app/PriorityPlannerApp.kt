package com.priorityplanner.app

import android.app.Application
import com.priorityplanner.app.data.local.AppDatabase
import com.priorityplanner.app.data.preferences.PreferenceManager
import com.priorityplanner.app.data.repository.TaskRepository
import com.priorityplanner.app.notification.NotificationHelper

class PriorityPlannerApp : Application() {

    lateinit var repository: TaskRepository
        private set

    lateinit var notificationHelper: NotificationHelper
        private set

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getDatabase(this)
        val preferenceManager = PreferenceManager(this)
        repository = TaskRepository(database.taskDao(), preferenceManager)

        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()
    }
}
