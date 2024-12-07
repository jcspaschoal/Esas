package com.pdm.esas.data.models

data class User(
    var id: String? = null,
    var email: String = "",
    var name: String = "",
    var password: String = "",
    var permissions: List<String> = emptyList(),
    var phone: String = ""
)
