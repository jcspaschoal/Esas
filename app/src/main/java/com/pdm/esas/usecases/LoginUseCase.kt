package com.pdm.esas.usecases

import com.pdm.esas.data.repository.AuthRepository
import com.pdm.esas.data.repository.UserRepository
import com.pdm.esas.utils.response.AuthErrorResponse
import javax.inject.Inject

sealed class LoginResult {
    object Success : LoginResult()
    data class AuthError(val message: String) : LoginResult()
    data class GeneralError(val message: String) : LoginResult()
}

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): LoginResult {
        val loginResponse = authRepository.login(email, password)
        val user = loginResponse.getOrElse { exception ->
            val errorResponse = AuthErrorResponse.fromException(exception)
            return LoginResult.AuthError(errorResponse.message)
        }
        try {
            authRepository.saveUserToPreferences(user)
        } catch (e: Exception) {
            return LoginResult.GeneralError("Falha ao salvar dados do usuário: ${e.message}")
        }
        val propsResult = userRepository.setUserProps(user.uid)
        propsResult.onFailure { exception ->
            return LoginResult.GeneralError("Falha ao carregar dados do usuário: ${exception.message}")
        }
        return LoginResult.Success
    }
}
