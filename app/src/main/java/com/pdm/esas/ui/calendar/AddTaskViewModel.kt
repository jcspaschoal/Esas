package com.pdm.esas.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTaskState(
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTaskState())
    val state: StateFlow<AddTaskState> = _state

    fun createTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.createTask(task)
                _state.value = _state.value.copy(successMessage = "Tarefa criada com sucesso")
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Não foi possível realizar essa ação no momento, tente novamente em instantes")
            }
        }
    }

    fun clearSuccessMessage() {
        _state.value = _state.value.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

