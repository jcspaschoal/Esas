package com.pdm.esas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.models.Visitor
import com.pdm.esas.data.repository.TaskRepository.Companion
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VisitorRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
) {
    companion object {
        private const val COLLECTION_NAME = "visitors"
    }

    private val visitorsCollection = firebaseStore.collection(COLLECTION_NAME)

    suspend fun createVisitor(visitor: Visitor): Result<String> {
        return try {
            val data = hashMapOf<String, Any?>()
            data["created_by"] = visitor.created_by
            data["name"] = visitor.name
            data["email"] = visitor.email
            data["phone"] = visitor.phone
            data["family_size"] = visitor.family_size
            data["description"] = visitor.description
            data["orders"] = visitor.orders
            data["nationality"] = visitor.nationality
            data["created_at"] = visitor.created_at
            data["updated_at"] = visitor.updated_at
            data["dates"] = visitor.dates
            val docRef = visitorsCollection.document()
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllVisitors(): Result<List<Visitor>> {
        return try {
            val snapshot = visitorsCollection.get().await()
            val list = snapshot.documents.mapNotNull {
                it.toObject(Visitor::class.java)?.copy(id = it.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

