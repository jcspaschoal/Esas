package com.pdm.esas.data.models

import com.google.firebase.Timestamp

data class Visit (
    var id: String? = null,
    var id_visitor: String? = null,
    var date: Timestamp? = null
)