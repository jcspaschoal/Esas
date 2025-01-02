package com.pdm.esas.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.esas.data.models.Task
import com.pdm.esas.ui.components.CalendarGrid
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    isAdmin: Boolean,
    viewModel: CalendarViewModel = hiltViewModel(),
    onEditTaskClick: (String) -> Unit = {},
    onViewDetailsClick: (String) -> Unit = {}
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    val monthName = currentMonth.month
        .getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
        .lowercase()
    val topBarTitle = "Tarefas de $monthName, ${currentMonth.year}"

    val dayOfWeek = selectedDate.dayOfWeek
        .getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
        .lowercase()
    val shortMonth = selectedDate.month
        .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        .lowercase()
    val dayLabel = "$dayOfWeek, ${selectedDate.dayOfMonth} de $shortMonth"

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(topBarTitle, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(dayLabel, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row {
                if (isAdmin) {
                    IconButton(onClick = onAddTaskClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                IconButton(onClick = { viewModel.loadTasksForDate(selectedDate) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        CalendarGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onPreviousMonth = { viewModel.onPreviousMonth() },
            onNextMonth = { viewModel.onNextMonth() },
            onDateClick = { day, yearMonth ->
                viewModel.onDateSelected(day, yearMonth)
            }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            items(tasks) { task: Task ->
                viewModel.userId?.let { currentUserId ->
                    TaskCard(
                        task = task,
                        currentUserId = currentUserId,
                        isAdmin = isAdmin,
                        onTaskUpdated = { updatedTask ->
                            viewModel.updateTaskInList(updatedTask)
                        },
                        onEditClick = {
                            task.id?.let { taskId -> onEditTaskClick(taskId) }
                        },
                        onViewDetails = {
                            task.id?.let { taskId -> onViewDetailsClick(taskId) }
                        }
                    )
                }
            }
        }
    }
}
