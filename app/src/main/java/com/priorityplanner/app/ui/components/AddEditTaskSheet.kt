package com.priorityplanner.app.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.priorityplanner.app.data.model.Priority
import com.priorityplanner.app.data.model.SubTaskEntity
import com.priorityplanner.app.data.model.TaskWithSubTasks
import com.priorityplanner.app.data.model.ThirdColorOption
import com.priorityplanner.app.ui.theme.getPriorityColors
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
    initialTask: TaskWithSubTasks? = null,
    targetDate: String,
    thirdColorOption: ThirdColorOption,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        priority: Priority,
        date: String,
        dueTime: String?,
        hasReminder: Boolean,
        reminderTimeMs: Long?,
        subtasks: List<String>
    ) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(initialTask?.task?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.task?.description ?: "") }
    var selectedPriority by remember {
        mutableStateOf(
            if (initialTask != null) Priority.fromLevel(initialTask.task.priority) else Priority.RED
        )
    }

    var dueTimeText by remember { mutableStateOf(initialTask?.task?.dueTime ?: "") }
    var hasReminder by remember { mutableStateOf(initialTask?.task?.hasReminder ?: false) }
    var reminderTimeMs by remember { mutableStateOf(initialTask?.task?.reminderTimeMs) }

    var newSubtaskText by remember { mutableStateOf("") }
    val subtasks = remember {
        mutableStateListOf<String>().apply {
            if (initialTask != null) {
                addAll(initialTask.subTasks.map { it.title })
            }
        }
    }

    val activeColors = getPriorityColors(selectedPriority.level, thirdColorOption, isDark)

    fun showTimePicker() {
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                dueTimeText = formatted

                // Compute reminder epoch ms for target date
                try {
                    val localDate = LocalDate.parse(targetDate)
                    val localTime = LocalTime.of(hourOfDay, minute)
                    val ldt = LocalDateTime.of(localDate, localTime)
                    reminderTimeMs = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } catch (_: Exception) {
                    val c = Calendar.getInstance()
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    c.set(Calendar.MINUTE, minute)
                    c.set(Calendar.SECOND, 0)
                    reminderTimeMs = c.timeInMillis
                }
            },
            currentHour,
            currentMinute,
            true
        ).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialTask == null) "New Task" else "Edit Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Priority selection cards
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SELECT PRIORITY LEVEL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        PrioritySelectionCard(
                            priority = Priority.RED,
                            selectedPriority = selectedPriority,
                            thirdColorOption = thirdColorOption,
                            isDark = isDark,
                            onClick = { selectedPriority = Priority.RED }
                        )

                        PrioritySelectionCard(
                            priority = Priority.YELLOW,
                            selectedPriority = selectedPriority,
                            thirdColorOption = thirdColorOption,
                            isDark = isDark,
                            onClick = { selectedPriority = Priority.YELLOW }
                        )

                        PrioritySelectionCard(
                            priority = Priority.COLOR_3,
                            selectedPriority = selectedPriority,
                            thirdColorOption = thirdColorOption,
                            isDark = isDark,
                            onClick = { selectedPriority = Priority.COLOR_3 }
                        )
                    }
                }

                // Title Input
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        placeholder = { Text("e.g., Complete critical project report") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Description Input
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notes / Details (Optional)") },
                        placeholder = { Text("Add any extra notes or requirements...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Due Time & Alarm Reminder Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Scheduled Alarm Reminder",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (dueTimeText.isBlank()) "No time set" else "Alert at $dueTimeText",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = hasReminder,
                                onCheckedChange = { checked ->
                                    hasReminder = checked
                                    if (checked && dueTimeText.isBlank()) {
                                        showTimePicker()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = activeColors.color,
                                    checkedTrackColor = activeColors.containerColor
                                )
                            )
                        }

                        if (hasReminder) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { showTimePicker() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = "Pick Time",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (dueTimeText.isBlank()) "Choose Time" else "Time: $dueTimeText")
                            }
                        }
                    }
                }

                // Subtasks checklist builder
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "CHECKLIST / SUBTASKS (${subtasks.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newSubtaskText,
                                onValueChange = { newSubtaskText = it },
                                placeholder = { Text("Add sub-step...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newSubtaskText.isNotBlank()) {
                                        subtasks.add(newSubtaskText.trim())
                                        newSubtaskText = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = activeColors.color)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                }

                itemsIndexed(subtasks) { index, subtaskTitle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${index + 1}. $subtaskTitle",
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { subtasks.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove subtask",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            title.trim(),
                            description.trim(),
                            selectedPriority,
                            targetDate,
                            if (dueTimeText.isBlank()) null else dueTimeText,
                            hasReminder,
                            if (hasReminder) reminderTimeMs else null,
                            subtasks.toList()
                        )
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColors.color,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (initialTask == null) "Create Priority Task" else "Update Task",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
