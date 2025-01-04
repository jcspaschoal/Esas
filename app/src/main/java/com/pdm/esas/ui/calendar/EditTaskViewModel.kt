package com.pdm.esas.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.models.UserStatus
import com.pdm.esas.data.repository.TaskRepository
import com.pdm.esas.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserPresenceItem(
    val userId: String,
    val name: String? = null,
    val isPresent: Boolean? = null
)

data class EditTaskUiState(
    val loadedTask: Task? = null,
    val userPresenceList: List<UserPresenceItem> = emptyList(),
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository // Injetamos o repositório de usuários
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            val result = taskRepository.getTask(taskId)
            if (result.isSuccess) {
                val task = result.getOrNull()
                if (task != null) {
                    // Para cada userId, pegamos o email no UserRepository
                    val userPresenceList = mutableListOf<UserPresenceItem>()
                    task.users?.forEach { (userId, status) ->
                        val userResult = userRepository.getUserById(userId)
                        val email = if (userResult.isSuccess) {
                            userResult.getOrNull()?.email ?: userId
                        } else {
                            userId // se der erro, mostra o userId
                        }
                        userPresenceList.add(
                            UserPresenceItem(
                                userId = userId,
                                name = email,
                                isPresent = status.isPresent
                            )
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        loadedTask = task,
                        userPresenceList = userPresenceList
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun markUserPresence(userId: String, isPresent: Boolean) {
        _uiState.update { current ->
            val newList = current.userPresenceList.map {
                if (it.userId == userId) it.copy(isPresent = isPresent) else it
            }
            current.copy(userPresenceList = newList)
        }
    }

    fun saveTask(title: String, description: String, limit: Int, date: Timestamp?) {
        val localState = _uiState.value
        val oldTask = localState.loadedTask ?: return
        val updatedUsers = oldTask.users?.toMutableMap()?.apply {
            localState.userPresenceList.forEach { item ->
                this[item.userId] = UserStatus(isPresent = item.isPresent)
            }
        }
        val updatedTask = oldTask.copy(
            title = title,
            description = description,
            task_limit = limit,
            task_date = date,
            users = updatedUsers
        )
        viewModelScope.launch {
            val updateResult = taskRepository.updateTask(updatedTask)
            if (updateResult.isSuccess) {
                _uiState.update {
                    it.copy(successMessage = "Tarefa atualizada com sucesso")
                }
            } else {
                val errorMsg = updateResult.exceptionOrNull()?.message ?: "Erro ao atualizar tarefa"
                _uiState.update {
                    it.copy(errorMessage = errorMsg)
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(successMessage = null)
        }
    }

    fun clearErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}
