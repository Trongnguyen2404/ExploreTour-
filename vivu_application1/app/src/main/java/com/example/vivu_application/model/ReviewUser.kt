package com.example.vivu_application.model

// Dữ liệu người dùng trong Review
data class ReviewUser(
    val id: Int,
    val fullName: String?,
    val profilePictureUrl: String? // URL ảnh đại diện (có thể null)
)