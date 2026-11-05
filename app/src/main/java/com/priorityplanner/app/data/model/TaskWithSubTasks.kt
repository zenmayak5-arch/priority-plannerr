package com.priorityplanner.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * TaskWithSubTasks is a Room Relation data class grouping a TaskEntity with its child SubTaskEntity items.
 */
data class TaskWithSubTasks(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subTasks: List<SubTaskEntity> = emptyList()
) {
    val totalSubtasksCount: Int
        get() = subTasks.size

    val completedSubtasksCount: Int
        get() = subTasks.count { it.isCompleted }

    val progress: Float
        get() = if (subTasks.isEmpty()) {
            if (task.isCompleted) 1.0f else 0.0f
        } else {
            completedSubtasksCount.toFloat() / totalSubtasksCount.toFloat()
        }
}
