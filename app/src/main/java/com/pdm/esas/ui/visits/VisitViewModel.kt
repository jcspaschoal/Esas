package com.pdm.esas.ui.visits

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitState(
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var nationality: String = "",
    var familySize: String = "",
    var description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VisitViewModel @Inject constructor() : ViewModel() {

    var state = mutableStateOf(VisitState())
        private set

    fun onNameChange(newValue: String) {
        state.value = state.value.copy(name = newValue)
    }

    fun onEmailChange(newValue: String) {
        state.value = state.value.copy(email = newValue)
    }

    fun onPhoneNumberChange(newValue: String) {
        state.value = state.value.copy(phoneNumber = newValue)
    }

    fun onNationalityChange(newValue: String) {
        state.value = state.value.copy(nationality = newValue)
    }

    fun onFamilySizeChange(newValue: String) {
        state.value = state.value.copy(familySize = newValue)
    }

    fun onDescriptionChange(newValue: String) {
        state.value = state.value.copy(description = newValue)
    }

    fun saveVisit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (state.value.name.isEmpty() || state.value.email.isEmpty()) {
            onError("Os campos 'Nome' e 'Email' são obrigatórios.")
            return
        }

        state.value = state.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Simular salvamento de dados (chamada à API ou banco de dados)
                kotlinx.coroutines.delay(1000)
                state.value = state.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                state.value = state.value.copy(isLoading = false)
                onError("Erro ao guardar os dados: ${e.message}")
            }
        }
    }
}
