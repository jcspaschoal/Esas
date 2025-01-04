package com.pdm.esas.ui.donations

import PaymentMethodDropdown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.pdm.esas.data.models.Donation
import com.pdm.esas.data.models.PaymentMethod
import com.pdm.esas.data.models.formatCurrencyEuro

@Composable
fun DonationItem(
    donation: Donation,
    onDelete: () -> Unit,
    onEdit: (String, Double, String, PaymentMethod) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editDonorName by remember { mutableStateOf(donation.donorName) }
    var editAmount by remember { mutableStateOf(donation.amount.toString()) }
    var editDescription by remember { mutableStateOf(donation.description) }
    var editPaymentMethod by remember { mutableStateOf(donation.paymentMethod) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (isEditing) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = editDonorName,
                    onValueChange = { editDonorName = it },
                    label = { Text("Editar nome do doador") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = editAmount,
                    onValueChange = { editAmount = it },
                    label = { Text("Editar valor da doação (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = editDescription,
                    onValueChange = { editDescription = it },
                    label = { Text("Editar descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaymentMethodDropdown(
                    selectedPaymentMethod = editPaymentMethod,
                    onPaymentMethodChange = { editPaymentMethod = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        val amountValue = editAmount.toDoubleOrNull()
                        if (amountValue != null) {
                            onEdit(editDonorName, amountValue, editDescription, editPaymentMethod)
                            isEditing = false
                        }
                    }) {
                        Text("Guardar alterações")
                    }
                    OutlinedButton(onClick = { isEditing = false }) {
                        Text("Cancelar")
                    }
                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Doador: ${donation.donorName}")
                Text(text = "Valor: ${formatCurrencyEuro(donation.amount)}")
                Text(text = "Meio de pagamento: ${donation.paymentMethod.displayName}")
                Text(text = "Descrição: ${donation.description}")
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = onDelete) {
                        Text("Remover")
                    }
                    OutlinedButton(onClick = { isEditing = true }) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}