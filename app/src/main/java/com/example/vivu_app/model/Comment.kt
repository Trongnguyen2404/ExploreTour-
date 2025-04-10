package com.example.vivu_app.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "User", // Tạm thời hardcode
    val content: String = "",
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)