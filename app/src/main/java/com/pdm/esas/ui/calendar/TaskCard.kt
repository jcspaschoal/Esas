package com.pdm.esas.ui.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Task
import com.pdm.esas.data.models.UserStatus
import com.pdm.esas.ui.task.SubscribeUiState
import com.pdm.esas.ui.task.TaskCardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskCard(
    task: Task,
    currentUserId: String,
    isAdmin: Boolean,
    onEditClick: () -> Unit = {},
    onViewDetails: () -> Unit = {},
    onTaskUpdated: (Task) -> Unit = {},
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    viewModel: TaskCardViewModel = hiltViewModel()
) {
    val subscribeUiState by viewModel.uiState.collectAsState()
    when (subscribeUiState) {
        SubscribeUiState.Idle -> {}
        SubscribeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        SubscribeUiState.Success -> {}
        is SubscribeUiState.Error -> {
            val msg = (subscribeUiState as SubscribeUiState.Error).message
            OrangeSnackBar(msg)
        }
        else -> {}
    }

    val spotsTaken = task.users?.size ?: 0
    val spotsTotal = task.task_limit ?: 0
    val isFull = spotsTaken >= spotsTotal
    val isUserInTask = task.users?.containsKey(currentUserId) == true
    var isExpanded by remember { mutableStateOf(false) }

    val formattedDate = task.task_date?.toDate()?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    } ?: "Data não definida"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title ?: "Sem título",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (isAdmin) {
                    Column(horizontalAlignment = Alignment.End) {
                        IconButton(onClick = onViewDetails) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    // Se não for admin, apenas exibe a data
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))
            if (isExpanded && !task.description.isNullOrEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val spotsColor = if (!isFull) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.error
                }
                Row(modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = spotsColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$spotsTaken/$spotsTotal",
                        style = MaterialTheme.typography.labelLarge.copy(color = spotsColor)
                    )
                }
                val buttonText: String
                val containerColor: Color
                val onButtonClick: () -> Unit
                if (isAdmin) {
                    buttonText = "Editar"
                    containerColor = Color(0xFFFF9800)
                    onButtonClick = onEditClick
                } else {
                    when {
                        isUserInTask -> {
                            buttonText = "Desinscrever"
                            containerColor = MaterialTheme.colorScheme.error
                            onButtonClick = onButtonClick@{
                                val id = task.id ?: return@onButtonClick
                                viewModel.unsubscribeTask(id, currentUserId) {
                                    val updatedUsers = task.users?.toMutableMap()?.also { map ->
                                        map.remove(currentUserId)
                                    } ?: emptyMap()
                                    onTaskUpdated(task.copy(users = updatedUsers))
                                }
                            }
                        }
                        isFull -> {
                            buttonText = "Completo"
                            containerColor = MaterialTheme.colorScheme.tertiary
                            onButtonClick = {}
                        }
                        else -> {
                            buttonText = "Inscrever"
                            containerColor = MaterialTheme.colorScheme.primary
                            onButtonClick = onButtonClick@{
                                val id = task.id ?: return@onButtonClick
                                viewModel.subscribeTask(id, currentUserId) {
                                    val updatedUsers = task.users?.toMutableMap()?.also { map ->
                                        map[currentUserId] = UserStatus(false)
                                    } ?: mapOf(currentUserId to UserStatus(false))
                                    onTaskUpdated(task.copy(users = updatedUsers))
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(36.dp)
                ) {
                    Text(buttonText)
                }
            }
            if (!task.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isExpanded) "Ver menos" else "Ver descrição",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OrangeSnackBar(text: String) {
    Snackbar(containerColor = Color(0xFFFF9800)) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
