package com.pdm.esas.data.models

import android.icu.text.NumberFormat

// Enum para representar os meios de pagamento
enum class PaymentMethod(val displayName: String) {
    MONEY("Dinheiro"),
    CHEQUE("Cheque"),
    MBWAY("MBWay")
}

// Modelo de dados de uma doação
data class Donation(
    val id: Int,
    val donorName: String,
    val amount: Double,
    val description: String,
    val paymentMethod: PaymentMethod
)

// Função para formatar o valor no formato Euro (€)
fun formatCurrencyEuro(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(java.util.Locale("pt", "PT"))
    return formatter.format(value)
}