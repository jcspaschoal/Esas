package com.pdm.esas.ui.visitors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Visitor
import java.util.Date


@Composable
fun VisitorView(modifier: Modifier = Modifier,
                viewModel: VisitorViewModel = hiltViewModel() ) {

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var family_size by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var orders by remember { mutableStateOf(TextFieldValue("")) }
    var nationality by remember { mutableStateOf(TextFieldValue("")) }
    var created_at by remember { mutableStateOf(TextFieldValue("")) }
    var updated_at by remember { mutableStateOf(TextFieldValue("")) }

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
            label = { Text("email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("phone") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = family_size,
            onValueChange = { family_size = it },
            label = { Text("family_size") },
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
            label = { Text("orders") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nationality,
            onValueChange = { nationality = it },
            label = { Text("nationality") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val now = Timestamp(Date())
                viewModel.createVisitor(
                    Visitor(
                        created_by =   viewModel.userId,
                        name = name.text,
                        email = email.text,
                        phone = phone.text,
                        family_size = family_size.text.toIntOrNull() ?: 0,
                        description = description.text,
                        orders = orders.text,
                        nationality = nationality.text,
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