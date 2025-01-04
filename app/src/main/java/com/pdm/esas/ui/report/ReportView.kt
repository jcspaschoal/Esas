package com.pdm.esas.ui.report

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReportView(modifier: Modifier = Modifier,  viewModel: ReportViewModel = hiltViewModel()) {

                val nationalityCountsState = remember { mutableStateOf<Map<String, Int>?>(null) }
                val errorState = remember { mutableStateOf<String?>(null) }

                // LaunchedEffect to fetch data
                LaunchedEffect(Unit) {
                    val result = viewModel.countNationalities()
                    if (result.isSuccess) {
                        nationalityCountsState.value = result.getOrNull()
                    } else if (result.isFailure) {
                        errorState.value = result.exceptionOrNull()?.message
                    }
                }

                // UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Nationality Counts (Pie Chart)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Show error message if there's an error
                    errorState.value?.let { error ->
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Show pie chart if data is available
                    nationalityCountsState.value?.let { nationalityCounts ->
                        if (nationalityCounts.isNotEmpty()) {
                            PieChart(
                                data = nationalityCounts.map { it.key to it.value },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(16.dp)
                            )
                        } else {
                            Text(text = "No data available.", style = MaterialTheme.typography.bodyMedium)
                        }
                    } ?: Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium
                    )
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

        // Legend
        Column(modifier = Modifier.fillMaxWidth()) {
            data.forEachIndexed { index, item ->
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
                    Text(text = "${item.first}: ${item.second}")
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


