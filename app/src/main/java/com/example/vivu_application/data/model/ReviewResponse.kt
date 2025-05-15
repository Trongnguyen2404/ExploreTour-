package com.example.vivu_application.data.model

// Cấu trúc response tổng thể từ API reviews (có phân trang)
data class ReviewResponse(
    val content: List<Review>,
    val pageNo: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)