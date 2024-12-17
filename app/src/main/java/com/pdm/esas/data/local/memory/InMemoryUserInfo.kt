package com.pdm.esas.data.local.memory

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class UserData(
    val userId: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userRoles: List<String>? = null
)

@Singleton
class InMemoryUserInfo @Inject constructor() {

    private val _userDataFlow = MutableStateFlow(UserData())
    val userDataFlow: StateFlow<UserData> = _userDataFlow

    fun setUserId(userId: String) {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userId = userId)
    }

    fun setUserEmail(email: String) {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userEmail = email)
    }

    fun setUserName(userName: String) {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userName = userName)
    }

    fun setUserRoles(roles: List<String>) {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userRoles = roles)
    }

    fun removeUserId() {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userId = null)
    }

    fun removeUserEmail() {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userEmail = null)
    }

    fun removeUserName() {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userName = null)
    }

    fun removeUserRoles() {
        val currentData = _userDataFlow.value
        _userDataFlow.value = currentData.copy(userRoles = null)
    }

    fun getUserId(): String? = _userDataFlow.value.userId
    fun getUserEmail(): String? = _userDataFlow.value.userEmail
    fun getUserName(): String? = _userDataFlow.value.userName
    fun getUserRoles(): List<String>? = _userDataFlow.value.userRoles
}
