package com.pdm.esas.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.local.memory.InMemoryUserInfo
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val inMemoryUserInfo: InMemoryUserInfo
) : ViewModel() {

    val userId: String? = inMemoryUserInfo.getUserId()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> get() = _currentMonth

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> get() = _selectedDate

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    fun onPreviousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }

    fun onNextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }

    fun onDateSelected(day: Int, yearMonth: YearMonth) {
        val date = yearMonth.atDay(day)
        _selectedDate.value = date
        _currentMonth.value = yearMonth
        loadTasksForDate(date)
    }

    fun loadTasksForDate(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = taskRepository.getAllTasksByDate(date)
            if (result.isSuccess) {
                _tasks.value = result.getOrNull().orEmpty()
            }
        }
    }

    fun updateTaskInList(updatedTask: Task) {
        _tasks.value = _tasks.value.map { oldTask ->
            if (oldTask.id == updatedTask.id) updatedTask else oldTask
        }
    }
}
