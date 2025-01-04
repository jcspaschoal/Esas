package com.pdm.esas.ui.donations

import PaymentMethodDropdown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.esas.data.models.PaymentMethod


@Composable
fun DonationView(
    modifier: Modifier = Modifier,
    viewModel: DonationViewModel = hiltViewModel()
) {
    var donorName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Gerir doações",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Formulário para adicionar doações
        TextField(
            value = donorName,
            onValueChange = { newValue ->
                // Permitir letras, espaços e caracteres acentuados
                if (newValue.isEmpty() || newValue.matches(Regex("^[\\p{L} .'-]*\$"))) {
                    donorName = newValue
                }
            },
            label = { Text("Nome do doador") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = amount,
            onValueChange = { newValue ->
                // Permitir apenas números e ponto decimal
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    amount = newValue
                }
            },
            label = { Text("Valor da doação (€)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição da doação") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown para selecionar o meio de pagamento
        PaymentMethodDropdown(
            selectedPaymentMethod = selectedPaymentMethod,
            onPaymentMethodChange = { selectedPaymentMethod = it }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val donationAmount = amount.toDoubleOrNull()
                if (donorName.isNotBlank() && donationAmount != null && description.isNotBlank() && selectedPaymentMethod != null) {
                    viewModel.addDonation(donorName, donationAmount, description, selectedPaymentMethod!!)
                    donorName = ""
                    amount = ""
                    description = ""
                    selectedPaymentMethod = null
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Adicionar doação")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de doações
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(viewModel.donations) { donation ->
                DonationItem(
                    donation = donation,
                    onDelete = { viewModel.deleteDonation(donation.id) },
                    onEdit = { donorName, amount, description, paymentMethod ->
                        viewModel.updateDonation(donation.id, donorName, amount, description, paymentMethod)
                    }
                )
            }
        }
    }
}