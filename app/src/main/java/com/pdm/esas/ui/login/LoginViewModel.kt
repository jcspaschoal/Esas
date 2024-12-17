package com.pdm.esas.ui.login


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.usecases.LoginResult
import com.pdm.esas.usecases.LoginUseCase
import com.pdm.esas.utils.log.Logger
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
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
        private const val GENERIC_MESSAGE =
            "Erro ao processar os dados do usuário. Por favor, tente novamente."
    }

    var state = mutableStateOf(LoginState())
        private set

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
        val email = state.value.username
        val password = state.value.password

        var hasError = false
        if (email.isEmpty()) {
            state.value = state.value.copy(emailError = "Email é obrigatório")
            hasError = true
        }
        if (password.isEmpty()) {
            state.value = state.value.copy(passwordError = "Senha é obrigatória")
            hasError = true
        }
        if (hasError) return

        state.value = state.value.copy(
            isLoading = true,
            error = null,
            emailError = null,
            passwordError = null
        )

        viewModelScope.launch {
            val result = loginUseCase(email, password)
            when (result) {
                is LoginResult.Success -> {
                    state.value = state.value.copy(isLoading = false)
                    onLoginSuccess()
                }

                is LoginResult.AuthError -> {
                    Logger.e(TAG, "Erro de autenticacao: ${result.message}")
                    state.value = state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is LoginResult.GeneralError -> {
                    Logger.e(TAG, "Erro ao salvar dados em memoria: ${result.message}")
                    state.value = state.value.copy(
                        isLoading = false,
                        error = GENERIC_MESSAGE
                    )
                }

                else -> {
                    state.value = state.value.copy(
                        isLoading = false,
                        error = GENERIC_MESSAGE
                    )
                }
            }
        }
    }
}

