package com.pdm.esas.ui.visitors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Visitor
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitorView(
    modifier: Modifier = Modifier,
    viewModel: VisitorViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var familySize by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var orders by remember { mutableStateOf("") }
    var selectedNationality by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = familySize,
            onValueChange = { familySize = it },
            label = { Text("Family Size") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = orders,
            onValueChange = { orders = it },
            label = { Text("Orders") },
            modifier = Modifier.fillMaxWidth()
        )

        // Nationality dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedNationality,
                onValueChange = {},
                readOnly = true,
                label = { Text("Nationality") },
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
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(nationality) },
                        onClick = {
                            selectedNationality = nationality
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val now = Timestamp(Date())
                viewModel.createVisitor(
                    Visitor(
                        created_by = viewModel.userId,
                        name = name,
                        email = email,
                        phone = phone,
                        family_size = familySize.toIntOrNull() ?: 0,
                        description = description,
                        orders = orders,
                        nationality = selectedNationality,
                        created_at = now,
                        updated_at = now
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Visitors")
        }
    }
}
