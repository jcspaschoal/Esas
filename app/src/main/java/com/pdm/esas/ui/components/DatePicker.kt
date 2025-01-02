package com.pdm.esas.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier,
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }
    var dateInput by remember {
        mutableStateOf(selectedDate?.let {
            convertMillisToDate(it, "dd/MM/yyyy")
        } ?: "")
    }
    var isDateValid by remember { mutableStateOf(true) }

    OutlinedTextField(
        value = dateInput,
        onValueChange = { input ->
            val formattedInput = formatDateString(input)
            dateInput = formattedInput

            // Valida a data formatada
            isDateValid = validateAndConvertDate(formattedInput)?.let { validDate ->
                onDateSelected(validDate)
                true
            } ?: false
        },
        label = { Text("Data") },
        placeholder = { Text("dd/MM/yyyy") },
        isError = !isDateValid,
        trailingIcon = {
            IconButton(onClick = { showModal = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Selecionar data")
            }
        },
        modifier = modifier.fillMaxWidth()
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = { date ->
                date?.let {
                    onDateSelected(it)
                    dateInput = convertMillisToDate(it, "dd/MM/yyyy")
                }
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}

fun formatDateString(input: String): String {
    // Remove caracteres não numéricos
    val digits = input.filter { it.isDigit() }

    // Formata dinamicamente: dd/MM/yyyy
    return when {
        digits.length <= 2 -> digits
        digits.length <= 4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
        digits.length <= 8 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
        else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
    }
}

fun validateAndConvertDate(date: String): Long? {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.isLenient = false
        val parsedDate = formatter.parse(date)
        parsedDate?.time
    } catch (e: Exception) {
        null
    }
}

fun convertMillisToDate(millis: Long, format: String = "dd/MM/yyyy"): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
