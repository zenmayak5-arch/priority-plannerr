package com.priorityplanner.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.priorityplanner.app.data.model.SubTaskEntity
import com.priorityplanner.app.data.model.TaskEntity
import com.priorityplanner.app.data.model.TaskWithSubTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY isCompleted ASC, priority ASC, createdAt DESC")
    fun getTasksForDate(date: String): Flow<List<TaskWithSubTasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE date < :todayDate AND isCompleted = 0 ORDER BY date DESC, priority ASC")
    fun getUnfinishedTasksBeforeDate(todayDate: String): Flow<List<TaskWithSubTasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC, date DESC")
    fun getAllCompletedTasks(): Flow<List<TaskWithSubTasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskWithSubTasks?

    @Query("SELECT * FROM tasks WHERE hasReminder = 1 AND isCompleted = 0 AND reminderTimeMs > :currentTime")
    suspend fun getUpcomingReminders(currentTime: Long): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET date = :newDate WHERE id IN (:taskIds)")
    suspend fun rolloverTasks(taskIds: List<Long>, newDate: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTaskEntity): Long

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Delete
    suspend fun deleteSubTask(subTask: SubTaskEntity)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubTasksForTask(taskId: Long)

    @Query("UPDATE subtasks SET isCompleted = :isCompleted WHERE id = :subTaskId")
    suspend fun updateSubTaskCompletion(subTaskId: Long, isCompleted: Boolean)
}
