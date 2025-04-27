package com.example.vivu_application.data.model

// Dữ liệu cho một location từ API
data class Location(
    val id: Int,
    val title: String,
    val headerImageUrl: String?, // URL ảnh (tên field khác tour)
    val averageRating: Double // Rating (API trả về Int, nhưng dùng Double cho nhất quán)
)