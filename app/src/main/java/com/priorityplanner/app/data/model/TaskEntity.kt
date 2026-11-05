package com.priorityplanner.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TaskEntity represents an individual task entry in the SQLite Room Database.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Int = 1, // 1 = Red (Must Do), 2 = Yellow (Should Do), 3 = 3rd Color (Today)
    val date: String, // Format: YYYY-MM-DD
    val dueTime: String? = null, // e.g., "14:30"
    val hasReminder: Boolean = false,
    val reminderTimeMs: Long? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
