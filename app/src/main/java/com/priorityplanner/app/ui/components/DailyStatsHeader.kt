package com.priorityplanner.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.priorityplanner.app.data.model.TaskWithSubTasks
import com.priorityplanner.app.data.model.ThirdColorOption
import com.priorityplanner.app.ui.theme.getPriorityColors

@Composable
fun DailyStatsHeader(
    tasks: List<TaskWithSubTasks>,
    thirdColorOption: ThirdColorOption,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.task.isCompleted }
    val progress = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks.toFloat()

    val redTasks = tasks.filter { it.task.priority == 1 }
    val redCompleted = redTasks.count { it.task.isCompleted }

    val yellowTasks = tasks.filter { it.task.priority == 2 }
    val yellowCompleted = yellowTasks.count { it.task.isCompleted }

    val thirdTasks = tasks.filter { it.task.priority == 3 }
    val thirdCompleted = thirdTasks.count { it.task.isCompleted }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Focus Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (totalTasks == 0) "No tasks added yet" else "$completedTasks of $totalTasks completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown by priority cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityStatPill(
                    priority = 1,
                    completed = redCompleted,
                    total = redTasks.size,
                    thirdColorOption = thirdColorOption,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                PriorityStatPill(
                    priority = 2,
                    completed = yellowCompleted,
                    total = yellowTasks.size,
                    thirdColorOption = thirdColorOption,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                PriorityStatPill(
                    priority = 3,
                    completed = thirdCompleted,
                    total = thirdTasks.size,
                    thirdColorOption = thirdColorOption,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PriorityStatPill(
    priority: Int,
    completed: Int,
    total: Int,
    thirdColorOption: ThirdColorOption,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = getPriorityColors(priority, thirdColorOption, isDark)
    val label = when (priority) {
        1 -> "P1 Red"
        2 -> "P2 Yellow"
        else -> "P3 Today"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.containerColor)
            .border(1.dp, colors.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(colors.color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.contentColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$completed / $total",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.contentColor
            )
        }
    }
}
