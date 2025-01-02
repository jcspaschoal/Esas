package com.pdm.esas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.local.memory.InMemoryUserInfo
import com.pdm.esas.data.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore,
    private val inMemoryUserInfo: InMemoryUserInfo
) {
    companion object {
        private const val COLLECTION_NAME = "users"
    }

    suspend fun setUserProps(documentId: String): Result<Unit> {
        return try {
            val snapshot = firebaseStore.collection(COLLECTION_NAME)
                .document(documentId)
                .get()
                .await()
            if (!snapshot.exists()) {
                Result.failure(Exception("Documento não encontrado para o ID: $documentId"))
            } else {
                val name = snapshot.getString("name") ?: ""
                val permissions = snapshot.get("permissions") as? List<String> ?: emptyList()
                inMemoryUserInfo.setUserName(name)
                inMemoryUserInfo.setUserRoles(permissions)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Exemplo de função para obter o usuário por ID
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val snapshot = firebaseStore.collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .await()
            if (!snapshot.exists()) {
                Result.failure(Exception("Usuário não encontrado: $userId"))
            } else {
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    // Ajusta o ID no objeto se quiser
                    user.id = snapshot.id
                    Result.success(user)
                } else {
                    Result.failure(Exception("Falha ao converter o usuário"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
