package com.pdm.esas.ui.visitors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.esas.data.models.Visitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVisitorView(
    modifier: Modifier = Modifier,
    visitorId: String,
    viewModel: EditVisitorViewModel = hiltViewModel(),
    onEditVisitorClick: () -> Unit
) {
    val visitor by viewModel.visitor

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var familySize by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var orders by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(visitorId) {
        viewModel.fetchVisitorById(visitorId)
    }

    // Atualiza os campos locais quando o visitante é carregado
    LaunchedEffect(visitor) {
        visitor?.let {
            name = it.name ?: ""
            email = it.email ?: ""
            phone = it.phone ?: ""
            familySize = it.family_size?.toString() ?: ""
            description = it.description ?: ""
            orders = it.orders ?: ""
            selectedNationality = it.nationality ?: ""
        }
    }

    if (visitor != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Editar Visitante",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it }, // Atualiza o estado local
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it }, // Atualiza o estado local
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it }, // Atualiza o estado local
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = familySize,
                onValueChange = { familySize = it }, // Atualiza o estado local
                label = { Text("Tamanho da Família") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it }, // Atualiza o estado local
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = orders,
                onValueChange = { orders = it }, // Atualiza o estado local
                label = { Text("Pedidos") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedNationality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nacionalidade") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.nationalities.forEach { nationality ->
                        DropdownMenuItem(
                            text = { Text(nationality) },
                            onClick = {
                                selectedNationality = nationality // Atualiza o estado local
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedVisitor = visitor?.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        family_size = familySize.toIntOrNull(),
                        description = description,
                        orders = orders,
                        nationality = selectedNationality
                    )
                    if (updatedVisitor != null) {
                        viewModel.updateVisitor(updatedVisitor)
                    }
                    onEditVisitorClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Alterações")
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

