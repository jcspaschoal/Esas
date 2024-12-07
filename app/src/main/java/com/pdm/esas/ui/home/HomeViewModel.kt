package com.pdm.esas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin.asStateFlow()

    init {
        viewModelScope.launch {
            val roles = userPreferences.getUserRoles()
            _isAdmin.value = roles?.contains("admin") == true
        }
    }
}
