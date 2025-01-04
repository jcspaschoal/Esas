package com.pdm.esas.ui.visitors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.local.memory.InMemoryUserInfo
import com.pdm.esas.data.models.Visitor
import com.pdm.esas.data.repository.VisitorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitorState(
    val successMessage: String? = null,
    val errorMessage: String? = null
)


@HiltViewModel
class VisitorViewModel @Inject constructor(
        private val repository: VisitorRepository,
        private val inMemoryUserInfo: InMemoryUserInfo
    ) : ViewModel() {

        val userId: String? = inMemoryUserInfo.getUserId()

        private val _state = MutableStateFlow(VisitorState())
        val state: StateFlow<VisitorState> = _state

        fun createVisitor(visitor: Visitor) {
            viewModelScope.launch {
                try {
                    repository.createVisitor(visitor)
                    _state.value = _state.value.copy(successMessage = "Visitante criado com successo")
                } catch (e: Exception) {
                    _state.value = _state.value.copy(errorMessage = "Não foi possível realizar essa ação no momento, tente novamente em instantes")
                }
            }
        }
    }
