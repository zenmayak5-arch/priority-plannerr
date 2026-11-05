package com.priorityplanner.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priorityplanner.app.data.model.Priority
import com.priorityplanner.app.data.model.SubTaskEntity
import com.priorityplanner.app.data.model.TaskEntity
import com.priorityplanner.app.data.model.TaskWithSubTasks
import com.priorityplanner.app.data.model.ThirdColorOption
import com.priorityplanner.app.data.repository.TaskRepository
import com.priorityplanner.app.notification.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskViewModel(
    private val repository: TaskRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    val todayDate: String = LocalDate.now().format(dateFormatter)

    private val _selectedDate = MutableStateFlow(todayDate)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    val priorityFilter: StateFlow<Priority?> = _priorityFilter.asStateFlow()

    val thirdColorOption: StateFlow<ThirdColorOption> = repository.thirdColorOption
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThirdColorOption.EMERALD_GREEN)

    val themeMode: StateFlow<String> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val isRolloverPromptEnabled: StateFlow<Boolean> = repository.isRolloverPromptEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val unfinishedPastTasks: StateFlow<List<TaskWithSubTasks>> = repository.getUnfinishedTasksBeforeDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rawTasksForSelectedDate = _selectedDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }

    // Filtered and searched tasks
    val filteredTasks: StateFlow<List<TaskWithSubTasks>> = combine(
        rawTasksForSelectedDate,
        _searchQuery,
        _priorityFilter
    ) { tasks, query, filter ->
        tasks.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.task.title.contains(query, ignoreCase = true) ||
                    item.task.description.contains(query, ignoreCase = true) ||
                    item.subTasks.any { it.title.contains(query, ignoreCase = true) }

            val matchesPriority = filter == null || item.task.priority == filter.level

            matchesQuery && matchesPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompletedTasks: StateFlow<List<TaskWithSubTasks>> = repository.getAllCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPriorityFilter(priority: Priority?) {
        _priorityFilter.value = priority
    }

    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        date: String,
        dueTime: String?,
        hasReminder: Boolean,
        reminderTimeMs: Long?,
        subtasks: List<String>
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.trim(),
                description = description.trim(),
                priority = priority.level,
                date = date,
                dueTime = dueTime,
                hasReminder = hasReminder,
                reminderTimeMs = reminderTimeMs,
                isCompleted = false
            )
            val taskId = repository.createTaskWithSubtasks(task, subtasks)

            if (hasReminder && reminderTimeMs != null) {
                notificationHelper.scheduleTaskReminder(
                    taskId = taskId,
                    title = title,
                    priority = priority.level,
                    triggerAtMillis = reminderTimeMs
                )
            }
        }
    }

    fun updateTask(
        task: TaskEntity,
        newSubtasks: List<SubTaskEntity>
    ) {
        viewModelScope.launch {
            repository.updateTask(task)
            repository.updateSubtasks(task.id, newSubtasks)

            if (task.hasReminder && task.reminderTimeMs != null && !task.isCompleted) {
                notificationHelper.scheduleTaskReminder(
                    taskId = task.id,
                    title = task.title,
                    priority = task.priority,
                    triggerAtMillis = task.reminderTimeMs
                )
            } else {
                notificationHelper.cancelTaskReminder(task.id)
            }
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val newCompletion = !task.isCompleted
            repository.toggleTaskCompletion(task.id, newCompletion)

            if (newCompletion) {
                notificationHelper.cancelTaskReminder(task.id)
                notificationHelper.cancelNotification(task.id)
            } else if (task.hasReminder && task.reminderTimeMs != null) {
                notificationHelper.scheduleTaskReminder(
                    taskId = task.id,
                    title = task.title,
                    priority = task.priority,
                    triggerAtMillis = task.reminderTimeMs
                )
            }
        }
    }

    fun toggleSubTaskCompletion(subTask: SubTaskEntity) {
        viewModelScope.launch {
            repository.toggleSubTaskCompletion(subTask.id, !subTask.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            notificationHelper.cancelTaskReminder(task.id)
            notificationHelper.cancelNotification(task.id)
            repository.deleteTask(task)
        }
    }

    fun rolloverAllPastTasks() {
        viewModelScope.launch {
            val overdue = unfinishedPastTasks.value
            if (overdue.isNotEmpty()) {
                val ids = overdue.map { it.task.id }
                repository.rolloverTasks(ids, todayDate)
            }
        }
    }

    fun rolloverSingleTask(taskId: Long) {
        viewModelScope.launch {
            repository.rolloverTasks(listOf(taskId), todayDate)
        }
    }

    fun setThirdColor(option: ThirdColorOption) {
        viewModelScope.launch {
            repository.setThirdColor(option)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun setRolloverPromptEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRolloverPromptEnabled(enabled)
        }
    }
}
