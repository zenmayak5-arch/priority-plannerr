package com.priorityplanner.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.priorityplanner.app.data.repository.TaskRepository
import com.priorityplanner.app.notification.NotificationHelper

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository, notificationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
