package com.pdm.esas.ui.visitors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Visitor
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitorView(
    modifier: Modifier = Modifier,
    viewModel: VisitorViewModel = hiltViewModel(),
    onCreateVisitorClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var familySize by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var orders by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Estados para erros
    var nameError by remember { mutableStateOf(true) }
    var emailError by remember { mutableStateOf(true) }
    var phoneError by remember { mutableStateOf(true) }
    var familySizeError by remember { mutableStateOf(true) }
    var nationalityError by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Centralizar o texto "Visita"
            Text(
                text = "Visitante",
                style = MaterialTheme.typography.headlineLarge,
                color = com.pdm.esas.ui.theme.primaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
            )

            // Ícone alinhado à direita
            IconButton(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = com.pdm.esas.ui.theme.primaryLight,
                )
            }
        }

        Column {
            // Campo Nome
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = it.isBlank()
                },
                label = { Text("Nome") },
                isError = nameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError) {
                Text(
                    text = "O nome não pode estar vazio.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !it.contains("@") || it.isBlank()
                },
                label = { Text("Email") },
                isError = emailError,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError) {
                Text(
                    text = "Insira um email válido.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo Telefone
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = it.length != 9 || it.any { char -> !char.isDigit() }
                },
                label = { Text("Telefone") },
                isError = phoneError,
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError) {
                Text(
                    text = "O número deve conter exatamente 9 dígitos e apenas números.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo Tamanho da Família
            OutlinedTextField(
                value = familySize,
                onValueChange = {
                    familySize = it
                    familySizeError = it.toIntOrNull() == null || it.toInt() <= 0
                },
                label = { Text("Tamanho da Família") },
                isError = familySizeError,
                modifier = Modifier.fillMaxWidth()
            )
            if (familySizeError) {
                Text(
                    text = "O tamanho da família deve ser maior que 0.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Campo Descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Pedidos
            OutlinedTextField(
                value = orders,
                onValueChange = { orders = it },
                label = { Text("Pedidos") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown Nacionalidade
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
                                selectedNationality = nationality
                                nationalityError = false
                                expanded = false
                            }
                        )
                    }
                }
            }
            if (nationalityError) {
                Text(
                    text = "Selecione uma nacionalidade válida.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Criar Visitante
        Button(
            onClick = {
                val now = Timestamp(Date())
                if (!nameError && !emailError && !phoneError && !familySizeError && !nationalityError) {
                    viewModel.createVisitor(
                        Visitor(
                            created_by = viewModel.userId,
                            name = name,
                            email = email,
                            phone = phone,
                            family_size = familySize.toInt(),
                            description = description,
                            orders = orders,
                            nationality = selectedNationality,
                            created_at = now,
                            updated_at = now
                        )
                    )
                    onCreateVisitorClick()
                }
            },
            enabled = !nameError && !emailError && !phoneError && !familySizeError && !nationalityError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Visitante")
        }

    }
}


