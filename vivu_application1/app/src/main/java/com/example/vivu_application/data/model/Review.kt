package com.example.vivu_application.data.model

import com.example.vivu_application.model.ReviewUser

// Dữ liệu cho một Review từ API
data class Review(
    val id: Int,
    val user: ReviewUser?, // Thông tin người dùng (có thể null?)
    val targetType: String, // "TOUR" hoặc "LOCATION"
    val targetId: Int,
    val rating: Int, // Số sao đánh giá
    val comment: String?, // Nội dung bình luận
    val createdAt: String?, // Thời gian tạo (String, cần format sau)
    val updatedAt: String? // Thời gian cập nhật
)