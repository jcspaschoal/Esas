package com.pdm.esas.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        val USER_ID = stringPreferencesKey("USER_ID")
        val USER_NAME = stringPreferencesKey("USER_NAME")
        val USER_EMAIL = stringPreferencesKey("USER_EMAIL")
        val USER_ROLES = stringPreferencesKey("USER_ROLES")
    }

    private suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T? = null): T? {
        return dataStore.data.map { it[key] ?: defaultValue }.first()
    }

    private suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { it[key] = value }
    }

    private suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit { it.remove(key) }
    }

    suspend fun getUserId(): String? = getPreference(USER_ID)
    suspend fun setUserId(userId: String) = setPreference(USER_ID, userId)
    suspend fun removeUserId() = removePreference(USER_ID)

    suspend fun getUserEmail(): String? = getPreference(USER_EMAIL)
    suspend fun setUserEmail(email: String) = setPreference(USER_EMAIL, email)
    suspend fun removeUserEmail() = removePreference(USER_EMAIL)

    suspend fun getUserName(): String? = getPreference(USER_NAME)
    suspend fun setUserName(userName: String) = setPreference(USER_NAME, userName)
    suspend fun removeUserName() = removePreference(USER_NAME)

    suspend fun getUserRoles(): List<String>? {
        val rolesString = getPreference(USER_ROLES)
        return rolesString?.split(",")
    }

    suspend fun setUserRoles(roles: List<String>) {
        val rolesString = roles.joinToString(",")
        setPreference(USER_ROLES, rolesString)
    }

    suspend fun removeUserRoles() = removePreference(USER_ROLES)

}