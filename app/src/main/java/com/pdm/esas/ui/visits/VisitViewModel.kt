package com.pdm.esas.ui.visits

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.models.Visit
import com.pdm.esas.data.models.Visitor
import com.pdm.esas.data.repository.VisitRepository
import com.pdm.esas.data.repository.VisitorRepository
import com.pdm.esas.ui.visitors.VisitorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitState(
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class VisitViewModel @Inject constructor(
    private val repository: VisitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VisitState())
    val state: StateFlow<VisitState> = _state

    fun createVisit(visit: Visit) {
        viewModelScope.launch {
            try {
                repository.createVisit(visit)
                _state.value = _state.value.copy(successMessage = "Visita criada com successo")
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Não foi possível realizar essa ação no momento, tente novamente em instantes")
            }
        }
    }
}