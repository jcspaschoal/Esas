package com.pdm.esas.ui.visits

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pdm.esas.data.models.Visit
import com.pdm.esas.data.models.Visitor

@Composable
fun VisitView(
    modifier: Modifier = Modifier,
    viewModel: VisitViewModel = hiltViewModel(),
) {
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<Visitor>()) }
    var selectedVisitor by remember { mutableStateOf<Visitor?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                performSearch(query) { results ->
                    searchResults = results
                }
            },
            label = { Text("Search by name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display Search Results
        LazyColumn(
            modifier = Modifier.weight(1f) // Permitir que a lista ocupe espaço restante
        ) {
            items(searchResults) { visitor ->
                val isSelected = visitor == selectedVisitor
                Text(
                    text = "${visitor.name} (phone: ${visitor.phone}, nationality: ${visitor.nationality})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { selectedVisitor = visitor }
                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Selected Visitor
        selectedVisitor?.let {
            Text(
                text = "Selected: ${it.name} (phone: ${it.phone}, nationality: ${it.nationality})",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                if (selectedVisitor != null) {
                    viewModel.createVisit(
                        Visit(
                            id_visitor = selectedVisitor?.id,
                            date = Timestamp.now()
                        )
                    )
                }
            },
            enabled = selectedVisitor != null, // Botão só é habilitado quando um visitante é selecionado
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}

fun performSearch(query: String, onResult: (List<Visitor>) -> Unit) {
    if (query.isEmpty()) {
        onResult(emptyList())
        return
    }

    val db = Firebase.firestore
    db.collection("visitors")
        .orderBy("name")
        .startAt(query)
        .endAt(query + "\uf8ff")
        .get()
        .addOnSuccessListener { documents ->
            val results = documents.mapNotNull { doc ->
                doc.toObject(Visitor::class.java)?.copy(id = doc.id)
            }
            onResult(results)
        }
        .addOnFailureListener { exception ->
            Log.e("FirebaseSearch", "Error getting documents: ", exception)
            onResult(emptyList())
        }
}
