package com.pdm.esas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarGrid(
    modifier: Modifier = Modifier,
    initialMonth: YearMonth = YearMonth.now(),
    selectedDay: Int? = null,
    onDateClick: (Int, YearMonth) -> Unit = { _, _ -> }
) {
    var currentMonth by remember { mutableStateOf(initialMonth) }
    val today = YearMonth.now().atDay(1).dayOfMonth
    val daysOfWeek = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    if (dragAmount > 0) currentMonth = currentMonth.minusMonths(1)
                    else if (dragAmount < 0) currentMonth = currentMonth.plusMonths(1)
                }
            }
    ) {
        // Header com navegação entre meses
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Mês Anterior",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = currentMonth.formatMonthYear(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Mês Seguinte",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cabeçalho dos dias da semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Corpo do calendário
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
            val totalDaysInMonth = currentMonth.lengthOfMonth()

            // Dias do mês anterior
            val previousMonth = currentMonth.minusMonths(1)
            val daysInPreviousMonth = previousMonth.lengthOfMonth()
            for (day in (daysInPreviousMonth - firstDayOfWeek + 1)..daysInPreviousMonth) {
                item {
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            // Dias do mês atual
            (1..totalDaysInMonth).forEach { day ->
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onDateClick(day, currentMonth) }
                    ) {
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal,
                            color = if (day == selectedDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                        if (day >= today) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (day % 2 == 0) Color.Green else MaterialTheme.colorScheme.error,
                                        shape = CircleShape
                                    )
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Dias do próximo mês
            val daysFilled = firstDayOfWeek + totalDaysInMonth
            val remainingDays = 7 - (daysFilled % 7)
            if (remainingDays < 7) {
                for (day in 1..remainingDays) {
                    item {
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Função para formatar o mês e ano em português
fun YearMonth.formatMonthYear(): String {
    val month = this.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
        .replaceFirstChar { it.uppercase() }
    return "$month ${this.year}"
}
