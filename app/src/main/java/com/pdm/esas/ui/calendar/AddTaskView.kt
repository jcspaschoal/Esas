package com.pdm.esas.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Task
import com.pdm.esas.ui.components.DatePickerFieldToModal
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskView(
    onBackClick: () -> Unit,
    adminId: String,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var taskDate by remember { mutableStateOf<Long?>(null) }
    var taskLimit by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val state = viewModel.state.collectAsState().value
    var snackbarColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(state.successMessage, state.errorMessage) {
        when {
            state.successMessage != null -> {
                snackbarColor = Color(0xFF4CAF50)
                snackbarHostState.showSnackbar(state.successMessage)
                viewModel.clearSuccessMessage()
            }

            state.errorMessage != null -> {
                snackbarColor = Color(0xFFFF9800)
                snackbarHostState.showSnackbar(state.errorMessage)
                viewModel.clearErrorMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Nova Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(containerColor = snackbarColor) {
                        Text(data.visuals.message)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            DatePickerFieldToModal(
                modifier = Modifier.fillMaxWidth(),
                selectedDate = taskDate,
                onDateSelected = { taskDate = it }
            )

            OutlinedTextField(
                value = taskLimit,
                onValueChange = { taskLimit = it },
                label = { Text("Limite de usuários") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val now = Timestamp(Date())
                    val dateTimestamp = taskDate?.let { Timestamp(Date(it)) }
                    viewModel.createTask(
                        Task(
                            created_by = adminId,
                            title = title.text,
                            description = description.text,
                            task_date = dateTimestamp,
                            task_limit = taskLimit.text.toIntOrNull() ?: 0,
                            created_at = now,
                            updated_at = now
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Tarefa")
            }
        }
    }
}
