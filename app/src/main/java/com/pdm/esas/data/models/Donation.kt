package com.pdm.esas.data.models

import android.icu.text.NumberFormat

// Enum para representar os meios de pagamento
enum class PaymentMethod(val displayName: String) {
    MONEY("Dinheiro"),
    CHEQUE("Cheque"),
    MBWAY("MBWay");

    companion object {
        fun fromValue(value: String): PaymentMethod? {
            return values().find { it.name == value }
        }
    }
}

// Modelo de dados de uma doação
data class Donation(
    val id: String = "", // Default value for no-argument constructor
    val donorName: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.MONEY // Default to a valid enum value
)

// Função para formatar o valor no formato Euro (€)
fun formatCurrencyEuro(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(java.util.Locale("pt", "PT"))
    return formatter.format(value)
}
