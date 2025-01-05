package com.pdm.esas.ui.donations

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.pdm.esas.data.models.Donation
import com.pdm.esas.data.models.PaymentMethod
import com.pdm.esas.data.repository.DonationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val repository: DonationsRepository
) : ViewModel() {

    private val _donations = mutableStateListOf<Donation>()
    private val _donationsFlow = MutableStateFlow<Result<List<Donation>>>(Result.success(emptyList()))
    val donations: StateFlow<Result<List<Donation>>> = _donationsFlow

    init {
        fetchDonations()
    }

    private fun fetchDonations() {
        viewModelScope.launch {
            repository.getAllDonations().onSuccess { donationList ->
                _donations.clear()
                _donations.addAll(donationList)
                _donationsFlow.emit(Result.success(donationList))
            }.onFailure { error ->
                _donationsFlow.emit(Result.failure(error))
            }
        }
    }

    fun addDonation(donorName: String, amount: Double, description: String, paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            try {
                // Log to ensure the function is triggered
                Log.d("DonationViewModel", "addDonation triggered with donorName: $donorName")

                val newDonation = Donation(
                    donorName = donorName,
                    amount = amount,
                    description = description,
                    paymentMethod = paymentMethod,
                    date = Timestamp.now()
                )

                repository.createDonations(newDonation).onSuccess {
                    // Log to confirm that donation was successfully created
                    Log.d("DonationViewModel", "Donation created successfully: ${newDonation.id}")
                    fetchDonations()  // Refresh the list of donations
                }.onFailure { error ->
                    Log.e("DonationViewModel", "Error creating donation: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("DonationViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun updateDonation(id: String, donorName: String, amount: Double, description: String, paymentMethod: PaymentMethod, date: Timestamp) {
        viewModelScope.launch {
            val updatedDonation = Donation(id, donorName, amount, description, paymentMethod, date)
            val index = _donations.indexOfFirst { it.id == id }
            if (index != -1) {
                _donations[index] = updatedDonation
                fetchDonations() // Ensure backend sync
            }
        }
    }

    fun deleteDonation(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteDonation(id).onSuccess {
                    _donations.removeIf { it.id == id }
                    fetchDonations() // Ensure backend sync
                }
            } catch (e: Exception) {
                // Handle exception (logging or user feedback)
            }
        }
    }
}

