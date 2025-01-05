package com.pdm.esas.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Donation
import com.pdm.esas.data.models.Visitor
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

    suspend fun updateVisitor(visitor: Visitor): Result<Unit> {
        return try {
            val visitorId = visitor.id ?: return Result.failure(IllegalArgumentException("Visitor ID cannot be null"))

            val data = hashMapOf<String, Any?>()
            data["name"] = visitor.name
            data["email"] = visitor.email
            data["phone"] = visitor.phone
            data["family_size"] = visitor.family_size
            data["description"] = visitor.description
            data["orders"] = visitor.orders
            data["nationality"] = visitor.nationality
            data["updated_at"] = Timestamp.now()

            visitorsCollection.document(visitorId).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVisitorById(visitorId: String): Result<Visitor> {
        return try {
            val document = visitorsCollection.document(visitorId).get().await()
            val visitor = document.toObject(Visitor::class.java)?.copy(id = document.id)
            if (visitor != null) {
                Result.success(visitor)
            } else {
                Result.failure(Exception("Visitante não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

