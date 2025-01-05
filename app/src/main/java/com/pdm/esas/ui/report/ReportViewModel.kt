package com.pdm.esas.ui.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Visit
import com.pdm.esas.data.models.VisitWithVisitor
import com.pdm.esas.data.models.Visitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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

    // Function to fetch visitor details by visitor ID
    suspend fun getVisitorById(visitorId: String): Result<Visitor> {
        return try {
            val visitorDoc = FirebaseFirestore.getInstance().collection("visitors").document(visitorId).get().await()
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

            // For each visit, fetch the visitor details and combine them
            val visitsWithVisitors = querySnapshot.documents.mapNotNull { visitDoc ->
                val visit = visitDoc.toObject(Visit::class.java)
                val visitorId = visit?.id_visitor

                if (visitorId != null) {
                    val visitorResult = getVisitorById(visitorId)
                    if (visitorResult.isSuccess) {
                        val visitor = visitorResult.getOrNull()
                        visit?.let {
                            VisitWithVisitor(it, visitor)
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            Result.success(visitsWithVisitors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    @Composable
    fun PieChart(data: List<Pair<String, Int>>, modifier: Modifier = Modifier) {
        val total = data.sumOf { it.second }
        val colors = generateDistinctColors(data.size)
        val proportions = data.map { it.second.toFloat() / total.toFloat() }
        val angles = proportions.map { it * 360f }

        Canvas(modifier = modifier) {
            var startAngle = 0f
            angles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend with percentages
        Column(modifier = Modifier.fillMaxWidth()) {
            data.forEachIndexed { index, item ->
                val percentage = (proportions[index] * 100).toInt() // Calculate percentage
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colors[index], CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${item.first}: ${item.second} (${percentage}%)")
                }
            }
        }
    }

    // Function to generate a list of distinct colors
    fun generateDistinctColors(count: Int): List<Color> {
        return List(count) { index ->
            val hue = (index * 360f / count) % 360
            Color.hsv(hue, 0.8f, 0.8f) // Adjust saturation and brightness as needed
        }
    }
}



