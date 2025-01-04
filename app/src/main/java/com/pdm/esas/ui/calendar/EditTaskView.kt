package com.pdm.esas.ui.calendar

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskView(
    onBackClick: () -> Unit,
    taskId: String,
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    val localUiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var taskLimit by remember { mutableStateOf(TextFieldValue("")) }
    var taskDate: Timestamp? by remember { mutableStateOf(null) }

    LaunchedEffect(localUiState) {
        // Se houver mensagem de sucesso, exibimos snackbar verde
        localUiState.successMessage?.let { successMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(successMsg)
                viewModel.clearSuccessMessage()
            }
        }
        // Se houver mensagem de erro, exibimos snackbar laranja
        localUiState.errorMessage?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMsg)
                viewModel.clearErrorMessage()
            }
        }
        // Se já carregou a tarefa, preenchermos os campos
        localUiState.loadedTask?.let { task ->
            title = TextFieldValue(task.title.orEmpty())
            description = TextFieldValue(task.description.orEmpty())
            taskLimit = TextFieldValue(task.task_limit?.toString().orEmpty())
            taskDate = task.task_date
        }
    }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data: SnackbarData ->
                // Decidimos a cor do container se é sucesso ou erro:
                val containerColor = if (data.visuals.message.contains("sucesso")) {
                    Color(0xFF4CAF50) // verde
                } else {
                    Color(0xFFFF9800) // laranja
                }
                Snackbar(containerColor = containerColor) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
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
                OutlinedTextField(
                    value = taskLimit,
                    onValueChange = { taskLimit = it },
                    label = { Text("Limite de usuários") },
                    modifier = Modifier.fillMaxWidth()
                )
                Divider()
                Text(
                    text = "Lista de Presença",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(localUiState.userPresenceList) { userItem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Exibe o email do usuário (name) em vez do userId
                                Text(text = userItem.name.orEmpty(), fontSize = 16.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = userItem.isPresent ?: false,
                                        onCheckedChange = { checked ->
                                            viewModel.markUserPresence(userItem.userId, checked)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.saveTask(
                            title.text,
                            description.text,
                            taskLimit.text.toIntOrNull() ?: 0,
                            taskDate
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Alterações")
                }
            }
        }
    }
}
