import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pdm.esas.data.models.PaymentMethod


@Composable
fun PaymentMethodDropdown(
    selectedPaymentMethod: PaymentMethod?,
    onPaymentMethodChange: (PaymentMethod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Botão principal com texto padrão ou selecionado
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedPaymentMethod?.displayName
                        ?: "Escolha o meio de pagamento da doação"
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir menu"
                )
            }
        }

        // Menu dropdown com as opções
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PaymentMethod.values().forEach { method ->
                DropdownMenuItem(
                    text = { Text(method.displayName) },
                    onClick = {
                        onPaymentMethodChange(method)
                        expanded = false
                    }
                )
            }
        }
    }
}