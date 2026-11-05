package com.priorityplanner.app.data.repository

import com.priorityplanner.app.data.local.TaskDao
import com.priorityplanner.app.data.model.SubTaskEntity
import com.priorityplanner.app.data.model.TaskEntity
import com.priorityplanner.app.data.model.TaskWithSubTasks
import com.priorityplanner.app.data.model.ThirdColorOption
import com.priorityplanner.app.data.preferences.PreferenceManager
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager
) {

    val thirdColorOption: Flow<ThirdColorOption> = preferenceManager.thirdColorOption
    val themeMode: Flow<String> = preferenceManager.themeMode
    val isRolloverPromptEnabled: Flow<Boolean> = preferenceManager.isRolloverPromptEnabled

    fun getTasksForDate(date: String): Flow<List<TaskWithSubTasks>> {
        return taskDao.getTasksForDate(date)
    }

    fun getUnfinishedTasksBeforeDate(todayDate: String): Flow<List<TaskWithSubTasks>> {
        return taskDao.getUnfinishedTasksBeforeDate(todayDate)
    }

    fun getAllCompletedTasks(): Flow<List<TaskWithSubTasks>> {
        return taskDao.getAllCompletedTasks()
    }

    suspend fun getTaskById(taskId: Long): TaskWithSubTasks? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun getUpcomingReminders(currentTime: Long): List<TaskEntity> {
        return taskDao.getUpcomingReminders(currentTime)
    }

    suspend fun createTaskWithSubtasks(
        task: TaskEntity,
        subtasks: List<String>
    ): Long {
        val taskId = taskDao.insertTask(task)
        if (subtasks.isNotEmpty()) {
            val entities = subtasks.mapIndexed { index, title ->
                SubTaskEntity(
                    taskId = taskId,
                    title = title.trim(),
                    isCompleted = false,
                    orderIndex = index
                )
            }
            taskDao.insertSubTasks(entities)
        }
        return taskId
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteSubTasksForTask(task.id)
        taskDao.deleteTask(task)
    }

    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.updateTaskCompletion(taskId, isCompleted, completedAt)
    }

    suspend fun rolloverTasks(taskIds: List<Long>, targetDate: String) {
        taskDao.rolloverTasks(taskIds, targetDate)
    }

    suspend fun addSubTask(taskId: Long, title: String, orderIndex: Int) {
        taskDao.insertSubTask(
            SubTaskEntity(
                taskId = taskId,
                title = title.trim(),
                isCompleted = false,
                orderIndex = orderIndex
            )
        )
    }

    suspend fun toggleSubTaskCompletion(subTaskId: Long, isCompleted: Boolean) {
        taskDao.updateSubTaskCompletion(subTaskId, isCompleted)
    }

    suspend fun deleteSubTask(subTask: SubTaskEntity) {
        taskDao.deleteSubTask(subTask)
    }

    suspend fun updateSubtasks(taskId: Long, updatedSubtasks: List<SubTaskEntity>) {
        taskDao.deleteSubTasksForTask(taskId)
        taskDao.insertSubTasks(updatedSubtasks)
    }

    suspend fun setThirdColor(option: ThirdColorOption) {
        preferenceManager.setThirdColor(option)
    }

    suspend fun setThemeMode(mode: String) {
        preferenceManager.setThemeMode(mode)
    }

    suspend fun setRolloverPromptEnabled(enabled: Boolean) {
        preferenceManager.setRolloverPromptEnabled(enabled)
    }
}
