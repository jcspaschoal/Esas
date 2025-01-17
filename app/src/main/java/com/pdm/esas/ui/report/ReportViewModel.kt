package com.pdm.esas.ui.report

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Visit
import com.pdm.esas.data.models.VisitWithVisitor
import com.pdm.esas.data.models.Visitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {

    suspend fun countNationalities(): Result<Map<String, Int>> {
        return try {
            val visitorsCollection = FirebaseFirestore.getInstance().collection("visitors")
            val querySnapshot = visitorsCollection.get().await()

            val nationalityCount = querySnapshot.documents
                .mapNotNull { it.getString("nationality") }
                .groupingBy { it }
                .eachCount()

            Result.success(nationalityCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVisitorById(visitorId: String): Result<Visitor> {
        return try {
            val visitorDoc = FirebaseFirestore.getInstance()
                .collection("visitors")
                .document(visitorId)
                .get()
                .await()

            val visitor = visitorDoc.toObject(Visitor::class.java)
            visitor?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Visitor not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun countVisits(): Result<List<VisitWithVisitor>> {
        return try {
            val visitsCollection = FirebaseFirestore.getInstance().collection("visits")
            val querySnapshot = visitsCollection.get().await()

            val visitsWithVisitors = querySnapshot.documents.mapNotNull { visitDoc ->
                val visit = visitDoc.toObject(Visit::class.java)
                val visitorId = visit?.id_visitor

                if (visitorId != null) {
                    val visitorResult = getVisitorById(visitorId)
                    if (visitorResult.isSuccess) {
                        visitorResult.getOrNull()?.let { visitor ->
                            VisitWithVisitor(visit, visitor)
                        }
                    } else null
                } else null
            }

            Result.success(visitsWithVisitors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
