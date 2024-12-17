package com.pdm.esas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.local.memory.InMemoryUserInfo
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
            val documentSnapshot = firebaseStore.collection(COLLECTION_NAME)
                .document(documentId)
                .get()
                .await()
            if (documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name") ?: ""
                val permissions =
                    documentSnapshot.get("permissions") as? List<String> ?: emptyList()
                inMemoryUserInfo.setUserName(name)
                inMemoryUserInfo.setUserRoles(permissions)
                inMemoryUserInfo.getUserRoles()
                inMemoryUserInfo.getUserName()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Documento não encontrado para o ID: $documentId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
