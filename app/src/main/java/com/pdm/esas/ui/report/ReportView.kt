package com.pdm.esas.ui.report

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.esas.data.models.Visit
import com.pdm.esas.data.models.VisitWithVisitor
import com.pdm.esas.ui.components.DatePickerFieldToModal
import java.util.Calendar

@Composable
fun ReportView(modifier: Modifier = Modifier, viewModel: ReportViewModel = hiltViewModel()) {
    val nationalityCountsState = remember { mutableStateOf<Map<String, Int>?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }
    val visitDatesState = remember { mutableStateOf<List<VisitWithVisitor>?>(null) }

    // LaunchedEffect to fetch nationality counts and visit data
    LaunchedEffect(Unit) {
        val nationalityResult = viewModel.countNationalities()
        if (nationalityResult.isSuccess) {
            nationalityCountsState.value = nationalityResult.getOrNull()
        } else if (nationalityResult.isFailure) {
            errorState.value = nationalityResult.exceptionOrNull()?.message
        }

        // Fetch visit data with visitor details
        val visitResult = viewModel.countVisits()
        if (visitResult.isSuccess) {
            visitDatesState.value = visitResult.getOrNull()
        } else if (visitResult.isFailure) {
            errorState.value = visitResult.exceptionOrNull()?.message
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            Text(
                text = "Contagem das Nacionalidades (Pie Chart)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        errorState.value?.let { error ->
            item {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        nationalityCountsState.value?.let { nationalityCounts ->
            if (nationalityCounts.isNotEmpty()) {
                item {
                    viewModel.PieChart(
                        data = nationalityCounts.map { it.key to it.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .padding(16.dp)
                    )
                }
            } else {
                item {
                    Text(text = "No data available.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        visitDatesState.value?.let { visitsWithVisitors ->
            if (visitsWithVisitors.isNotEmpty()) {
                item {
                    Text(
                        text = "Datas das Visitas",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                    )
                }

                // Sort visits by date in descending order (newest first)
                val sortedVisits = visitsWithVisitors.sortedByDescending { it.visit.date }

                items(sortedVisits) { visitWithVisitor ->
                    val visit = visitWithVisitor.visit
                    val visitor = visitWithVisitor.visitor
                    Text(
                        text = "Visitante: ${visitor?.name ?: "Unknown"} on ${visit.date?.toDate()?.toString() ?: "Unknown Date"}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                item {
                    Text(text = "No visits available.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } ?: item {
            Text(
                text = "A carregar visitas...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}











