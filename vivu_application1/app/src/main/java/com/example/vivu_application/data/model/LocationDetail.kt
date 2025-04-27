package com.example.vivu_application.data.model

// Dữ liệu chi tiết cho một Location từ API
data class LocationDetail(
    val id: Int,
    val title: String,
    val headerImageUrl: String?,
    val averageRating: Double,
    val contentBlocks: List<ContentBlock>?, // Danh sách các khối nội dung
    val reviews: List<Any>? // Kiểu dữ liệu cho review chưa rõ, tạm để Any
)