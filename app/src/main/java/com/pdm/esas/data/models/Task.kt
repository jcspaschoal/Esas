package com.pdm.esas.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName


data class Task(
    val id: String? = null,
    val created_by: String? = null,
    val title: String? = null,
    val description: String? = null,
    val task_limit: Int? = null,
    val task_date: Timestamp? = null,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null,
    val users: Map<String, UserStatus>? = null
)


data class UserStatus(
    @get:PropertyName("isPresent")
    @set:PropertyName("isPresent")
    var isPresent: Boolean? = null
)


fun Task.toMap(): Map<String, Any?> {
    return mapOf(
        "created_by" to created_by,
        "title" to title,
        "description" to description,
        "task_limit" to task_limit,
        "task_date" to task_date,
        "created_at" to created_at,
        "updated_at" to updated_at,
        "users" to users
    )
}
