package com.pdm.esas.ui.donations

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.pdm.esas.data.models.Donation
import com.pdm.esas.data.models.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor() : ViewModel() {

    private val _donations = mutableStateListOf<Donation>()
    val donations: List<Donation> get() = _donations

    // Adicionar uma nova doação
    fun addDonation(donorName: String, amount: Double, description: String, paymentMethod: PaymentMethod) {
        val newDonation = Donation(
            id = if (_donations.isEmpty()) 1 else _donations.maxOf { it.id } + 1,
            donorName = donorName,
            amount = amount,
            description = description,
            paymentMethod = paymentMethod
        )
        _donations.add(newDonation)
    }

    // Atualizar uma doação existente
    fun updateDonation(id: Int, donorName: String, amount: Double, description: String, paymentMethod: PaymentMethod) {
        val index = _donations.indexOfFirst { it.id == id }
        if (index != -1) {
            _donations[index] = Donation(id, donorName, amount, description, paymentMethod)
        }
    }

    // Remover uma doação
    fun deleteDonation(id: Int) {
        _donations.removeIf { it.id == id }
    }
}