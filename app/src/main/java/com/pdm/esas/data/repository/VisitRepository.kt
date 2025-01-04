package com.pdm.esas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Visit
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VisitRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
){
    companion object{
        private const val COLLECTION_NAME = "visits"
    }

    private val visitsCollection = firebaseStore.collection(COLLECTION_NAME)

    suspend fun createVisit(visit: Visit): Result<String>{
        return try {
            val data = hashMapOf<String, Any?>()
            data["id_visitor"] = visit.id_visitor
            data["date"] = visit.date
            val docRef = visitsCollection.document()
            docRef.set(data).await()
            Result.success(docRef.id)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}