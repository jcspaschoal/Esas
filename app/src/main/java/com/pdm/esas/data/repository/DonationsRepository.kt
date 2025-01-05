package com.pdm.esas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.models.Donation
import com.pdm.esas.data.models.Visitor
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DonationsRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
) {
    companion object {
        private const val COLLECTION_NAME = "donations"
    }

    private val donationsCollection = firebaseStore.collection(COLLECTION_NAME)

    suspend fun createDonations(donation: Donation): Result<String> {
        return try {
            val data = hashMapOf(
                "donorName" to donation.donorName,
                "amount" to donation.amount,
                "description" to donation.description,
                "paymentMethod" to donation.paymentMethod.name,
                "date" to donation.date
            )

            // Use Firestore's automatic document ID generation
            val docRef = donationsCollection.document() // Generates a new document with an auto-generated ID
            docRef.set(data).await()

            // Return the auto-generated document ID
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun getAllDonations(): Result<List<Donation>> {
        return try {
            val snapshot = donationsCollection.get().await()
            val list = snapshot.documents.mapNotNull {
                it.toObject(Donation::class.java)?.copy(id = it.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDonation(id: String): Result<Unit> {
        return try {
            donationsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
