package com.pdm.esas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pdm.esas.data.local.preferences.UserPreferences
import com.pdm.esas.utils.log.Logger
import com.pdm.esas.utils.response.AuthErrorResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// #TODO CENTRALIZAR MELHOR OS LOGS
@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences
) {

    companion object {
        private const val TAG = "AuthRepository"
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            Logger.d(TAG, "Tentando realizar login com o email: $email")

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val user = authResult.user ?: throw Exception("Usuário não encontrado após login")

            Logger.i(TAG, "Login realizado com sucesso para o email: $email")
            Result.success(user)
        } catch (exception: Exception) {
            val errorResponse = AuthErrorResponse.fromException(exception)
            Logger.e(TAG, "Falha no login: ${errorResponse.message}")
            Result.failure(exception)
        }
    }

    suspend fun saveUserToPreferences(user: FirebaseUser) {
        try {
            userPreferences.setUserEmail(user.email ?: "")
            userPreferences.setUserId(user.uid)
            Logger.i(TAG, "Dados do usuário salvos nas preferências")
        } catch (exception: Exception) {
            Logger.e(TAG, "Falha ao salvar os detalhes do usuário: ${exception.message}")
            throw exception
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            Logger.d(TAG, "Tentando realizar logout")

            firebaseAuth.signOut()


            // Remover detalhes do usuário nas preferências

            userPreferences.removeUserId()
            userPreferences.removeUserEmail()
            userPreferences.removeUserName()
            userPreferences.removeUserRoles()

            Logger.d(TAG, "Logout realizado com sucesso")
            Result.success(Unit)
        } catch (exception: Exception) {
            val errorResponse = AuthErrorResponse(
                AuthErrorResponse.Status.UNKNOWN,
                -100,
                "Falha no logout: ${exception.message ?: "Erro desconhecido"}"
            )
            Logger.e(TAG, errorResponse.message)
            Result.failure(exception)
        }
    }
}
