package com.pdm.esas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.local.preferences.UserPreferences
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore,
    private val userPreferences: UserPreferences
) {

    companion object {
        private const val TAG = "UserRepository"
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

                userPreferences.setUserName(name)
                userPreferences.setUserRoles(permissions)

                Result.success(Unit)
            } else {
                val errorMessage = "Documento não encontrado para o ID: $documentId"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
