package com.pdm.esas.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SubscribeUiState {
    object Idle : SubscribeUiState()
    object Loading : SubscribeUiState()
    object Success : SubscribeUiState()
    data class Error(val message: String) : SubscribeUiState()
}

@HiltViewModel
class TaskCardViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SubscribeUiState>(SubscribeUiState.Idle)
    val uiState: StateFlow<SubscribeUiState> = _uiState

    fun subscribeTask(documentId: String, userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = SubscribeUiState.Loading
            val result = taskRepository.subscribeTask(documentId, userId)
            if (result.isSuccess) {
                _uiState.value = SubscribeUiState.Success
                onSuccess()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Falha ao se inscrever."
                _uiState.update { SubscribeUiState.Error(errorMsg) }
            }
        }
    }

    fun unsubscribeTask(documentId: String, userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = SubscribeUiState.Loading
            val result = taskRepository.unsubscribeTask(documentId, userId)
            if (result.isSuccess) {
                _uiState.value = SubscribeUiState.Success
                onSuccess()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Falha ao se desinscrever."
                _uiState.update { SubscribeUiState.Error(errorMsg) }
            }
        }
    }
}
