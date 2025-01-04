package com.pdm.esas.data.models

import com.google.firebase.Timestamp

data class Visitor(
    var id: String? = null,
    var created_by: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var family_size: Int? = null,
    var description: String? = null,
    var orders: String? = null,
    var nationality: String? = null,
    var created_at: Timestamp? = null,
    var updated_at: Timestamp? = null,
    var dates: List<Timestamp>? = null
)