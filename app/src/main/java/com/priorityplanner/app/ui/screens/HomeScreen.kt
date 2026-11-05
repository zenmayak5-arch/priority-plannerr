package com.priorityplanner.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.priorityplanner.app.data.model.Priority
import com.priorityplanner.app.data.model.SubTaskEntity
import com.priorityplanner.app.data.model.TaskEntity
import com.priorityplanner.app.data.model.TaskWithSubTasks
import com.priorityplanner.app.ui.components.AddEditTaskSheet
import com.priorityplanner.app.ui.components.DailyStatsHeader
import com.priorityplanner.app.ui.components.RolloverBanner
import com.priorityplanner.app.ui.components.TaskCard
import com.priorityplanner.app.ui.theme.getPriorityColors
import com.priorityplanner.app.ui.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val unfinishedPastTasks by viewModel.unfinishedPastTasks.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedFilter by viewModel.priorityFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val thirdColorOption by viewModel.thirdColorOption.collectAsState()
    val isRolloverEnabled by viewModel.isRolloverPromptEnabled.collectAsState()

    val isDark = isSystemInDarkTheme()

    var showAddSheet by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskWithSubTasks?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var dismissedRollover by remember { mutableStateOf(false) }

    val parsedDate = remember(selectedDate) {
        try {
            LocalDate.parse(selectedDate)
        } catch (_: Exception) {
            LocalDate.now()
        }
    }

    val displayDateHeader = remember(selectedDate, viewModel.todayDate) {
        if (selectedDate == viewModel.todayDate) {
            "Today (${parsedDate.format(DateTimeFormatter.ofPattern("MMM d"))})"
        } else {
            parsedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
        }
    }

    fun stepDate(days: Long) {
        val newDate = parsedDate.plusDays(days).format(DateTimeFormatter.ISO_LOCAL_DATE)
        viewModel.setSelectedDate(newDate)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Priority Planner",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Red (Must) • Yellow (Important) • ${thirdColorOption.displayName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(
                            imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showAddSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Priority Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar (if visible)
            if (showSearchBar) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search by task title or subtask...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Date Navigation Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { stepDate(-1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.setSelectedDate(viewModel.todayDate)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayDateHeader,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { stepDate(1) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Day")
                    }
                }
            }

            // Rollover Banner if past tasks remain unfinished
            if (isRolloverEnabled && unfinishedPastTasks.isNotEmpty() && !dismissedRollover) {
                item {
                    RolloverBanner(
                        overdueTasks = unfinishedPastTasks,
                        onRolloverAll = {
                            viewModel.rolloverAllPastTasks()
                            dismissedRollover = true
                        },
                        onDismiss = { dismissedRollover = true }
                    )
                }
            }

            // Daily Progress Stats Header
            item {
                DailyStatsHeader(
                    tasks = tasks,
                    thirdColorOption = thirdColorOption
                )
            }

            // Filter Chips (All, Priority 1 Red, Priority 2 Yellow, Priority 3 Today)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { viewModel.setPriorityFilter(null) },
                            label = { Text("All (${tasks.size})") },
                            colors = FilterChipDefaults.filterChipColors()
                        )
                    }

                    item {
                        val p1Colors = getPriorityColors(1, thirdColorOption, isDark)
                        FilterChip(
                            selected = selectedFilter == Priority.RED,
                            onClick = {
                                viewModel.setPriorityFilter(
                                    if (selectedFilter == Priority.RED) null else Priority.RED
                                )
                            },
                            label = { Text("P1 Red (Must)") },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(p1Colors.color)
                                )
                            }
                        )
                    }

                    item {
                        val p2Colors = getPriorityColors(2, thirdColorOption, isDark)
                        FilterChip(
                            selected = selectedFilter == Priority.YELLOW,
                            onClick = {
                                viewModel.setPriorityFilter(
                                    if (selectedFilter == Priority.YELLOW) null else Priority.YELLOW
                                )
                            },
                            label = { Text("P2 Yellow (Important)") },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(p2Colors.color)
                                )
                            }
                        )
                    }

                    item {
                        val p3Colors = getPriorityColors(3, thirdColorOption, isDark)
                        FilterChip(
                            selected = selectedFilter == Priority.COLOR_3,
                            onClick = {
                                viewModel.setPriorityFilter(
                                    if (selectedFilter == Priority.COLOR_3) null else Priority.COLOR_3
                                )
                            },
                            label = { Text("P3 Today (${thirdColorOption.displayName})") },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(p3Colors.color)
                                )
                            }
                        )
                    }
                }
            }

            // Tasks List
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "No Tasks",
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No tasks planned for this day",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap the + button below to add your first priority task",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(tasks, key = { it.task.id }) { taskWithSubTasks ->
                    TaskCard(
                        taskWithSubTasks = taskWithSubTasks,
                        thirdColorOption = thirdColorOption,
                        onToggleTask = { viewModel.toggleTaskCompletion(it) },
                        onToggleSubTask = { viewModel.toggleSubTaskCompletion(it) },
                        onEditTask = {
                            taskToEdit = it
                            showAddSheet = true
                        },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onRolloverTask = { taskId ->
                            viewModel.rolloverSingleTask(taskId)
                        }
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AddEditTaskSheet(
            initialTask = taskToEdit,
            targetDate = selectedDate,
            thirdColorOption = thirdColorOption,
            onDismiss = {
                showAddSheet = false
                taskToEdit = null
            },
            onSave = { title, desc, priority, date, time, hasReminder, reminderMs, subtasks ->
                if (taskToEdit == null) {
                    viewModel.addTask(
                        title = title,
                        description = desc,
                        priority = priority,
                        date = date,
                        dueTime = time,
                        hasReminder = hasReminder,
                        reminderTimeMs = reminderMs,
                        subtasks = subtasks
                    )
                } else {
                    val currentTask = taskToEdit!!.task
                    val updatedTask = currentTask.copy(
                        title = title,
                        description = desc,
                        priority = priority.level,
                        date = date,
                        dueTime = time,
                        hasReminder = hasReminder,
                        reminderTimeMs = reminderMs
                    )
                    val updatedSubtasks = subtasks.mapIndexed { idx, subtaskTitle ->
                        SubTaskEntity(
                            taskId = currentTask.id,
                            title = subtaskTitle,
                            orderIndex = idx
                        )
                    }
                    viewModel.updateTask(updatedTask, updatedSubtasks)
                }
                showAddSheet = false
                taskToEdit = null
            }
        )
    }
}
