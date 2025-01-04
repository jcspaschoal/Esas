package com.pdm.esas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.local.memory.InMemoryUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val inMemoryUserInfo: InMemoryUserInfo
) : ViewModel() {

    private val _userRoles = MutableStateFlow<List<String>>(emptyList())
    val userRoles: StateFlow<List<String>> = _userRoles.asStateFlow()
    val userId = inMemoryUserInfo.getUserId() ?: ""

    init {
        viewModelScope.launch {
            val roles = inMemoryUserInfo.getUserRoles() ?: emptyList()
            val id = inMemoryUserInfo.getUserId() ?: ""
            _userRoles.value = roles
        }
    }

    fun hasRole(role: String): Boolean {
        return _userRoles.value.contains(role)
    }
}
