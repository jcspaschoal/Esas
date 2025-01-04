package com.pdm.esas.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.models.User
import com.pdm.esas.data.repository.TaskRepository
import com.pdm.esas.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReadOnlyPresenceUser(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val isPresent: Boolean
)

data class ReadOnlyPresenceUiState(
    val task: Task? = null,
    val users: List<ReadOnlyPresenceUser> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class PresenceViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadOnlyPresenceUiState())
    val uiState: StateFlow<ReadOnlyPresenceUiState> = _uiState

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val result = taskRepository.getTask(taskId)
            if (result.isSuccess) {
                val task = result.getOrNull()
                if (task != null) {
                    val presenceList = buildPresenceList(task)
                    _uiState.update {
                        it.copy(
                            task = task,
                            users = presenceList,
                            isLoading = false,
                            successMessage = "Lista carregada com sucesso!"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Falha ao processar a lista."
                        )
                    }
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erro ao carregar a lista."
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = errorMsg)
                }
            }
        }
    }

    private suspend fun buildPresenceList(task: Task): List<ReadOnlyPresenceUser> {
        val userList = mutableListOf<ReadOnlyPresenceUser>()
        val usersMap = task.users.orEmpty()
        for ((userId, status) in usersMap) {
            val userResult = userRepository.getUserById(userId)
            val userData: User? = userResult.getOrNull()
            userList.add(
                ReadOnlyPresenceUser(
                    userId = userId,
                    userName = userData?.name ?: userId,
                    userEmail = userData?.email ?: "Sem e-mail",
                    userPhone = userData?.phone ?: "Sem telefone",
                    isPresent = status.isPresent == true
                )
            )
        }
        return userList
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
