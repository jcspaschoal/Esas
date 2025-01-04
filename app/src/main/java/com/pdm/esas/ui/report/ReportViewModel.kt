package com.pdm.esas.ui.report

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {

    suspend fun countNationalities(): Result<Map<String, Int>> {
        return try {
            val visitorsCollection = FirebaseFirestore.getInstance().collection("visitors")
            val querySnapshot = visitorsCollection.get().await()

            // Extract nationalities and count occurrences
            val nationalityCount = querySnapshot.documents
                .mapNotNull { it.getString("nationality") } // Get the "nationality" field
                .groupingBy { it } // Group by nationality
                .eachCount() // Count occurrences

            Result.success(nationalityCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}