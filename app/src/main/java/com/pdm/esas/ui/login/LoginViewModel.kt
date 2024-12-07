package com.pdm.esas.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.repository.AuthRepository
import com.pdm.esas.data.repository.UserRepository
import com.pdm.esas.utils.log.Logger
import com.pdm.esas.utils.response.AuthErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    var username: String = "",
    var password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null, // Para erros gerais exibidos no Snackbar
    val emailError: String? = null, // Erro específico do email
    val passwordError: String? = null, // Erro específico da senha
    val passwordVisible: Boolean = false // Controla a visibilidade da senha
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    var state = mutableStateOf(LoginState())
        private set

    private val username
        get() = state.value.username
    private val password
        get() = state.value.password

    fun onUsernameChange(newValue: String) {
        state.value = state.value.copy(username = newValue, emailError = null)
    }

    fun onPasswordChange(newValue: String) {
        state.value = state.value.copy(password = newValue, passwordError = null)
    }

    fun onPasswordVisibilityChange() {
        state.value = state.value.copy(passwordVisible = !state.value.passwordVisible)
    }

    fun clearError() {
        state.value = state.value.copy(error = null)
    }

    fun login(onLoginSuccess: () -> Unit) {
        var hasError = false

        if (username.isEmpty()) {
            state.value = state.value.copy(emailError = "Email é obrigatório")
            hasError = true
        }
        if (password.isEmpty()) {
            state.value = state.value.copy(passwordError = "Senha é obrigatória")
            hasError = true
        }

        if (hasError) {
            return
        }

        state.value = state.value.copy(isLoading = true, error = null)
        // #TODO TERMINAR DE CENTRALIZAR O FLUXO DE LOGIN EM OUTRA CLASSE , DE PREFERENCIA INTEGRADO COM O SNACKABAR
        viewModelScope.launch {
            try {
                val loginResult = authRepository.login(username, password)
                loginResult.onSuccess { user ->
                    authRepository.saveUserToPreferences(user)

                    val rolesResult = userRepository.setUserProps(user.uid)
                    rolesResult.onSuccess {
                        state.value = state.value.copy(isLoading = false)
                        onLoginSuccess()
                    }.onFailure { exception ->
                        handleProcessingError(exception)
                    }
                }.onFailure { exception ->
                    val errorResponse = AuthErrorResponse.fromException(exception as Exception)
                    state.value = state.value.copy(
                        isLoading = false,
                        error = errorResponse.status.description
                    )
                }
            } catch (exception: Exception) {
                handleProcessingError(exception)
            }
        }
    }

    private suspend fun handleProcessingError(exception: Throwable) {
        Logger.e(TAG, "Erro no fluxo de login: ${exception.message}")

        authRepository.logout()

        state.value = state.value.copy(
            isLoading = false,
            error = "Erro ao processar os dados do usuário. Por favor, tente novamente."
        )
    }


}
