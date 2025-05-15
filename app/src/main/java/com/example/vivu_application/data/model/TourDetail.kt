package com.example.vivu_application.data.model

// Dữ liệu chi tiết cho một Tour từ API
data class TourDetail(
    val id: Int,
    val title: String,
    val mainImageUrl: String?,
    val locationName: String?,
    val itineraryDuration: String?,
    val departureDate: String?,
    val availableSlots: Int,
    val averageRating: Double,
    val tourCode: String?,
    val contactPhone: String?,
    val content: String?, // Nội dung mô tả chính (có thể là HTML)
    val scheduleImageUrl: String?, // URL ảnh lịch trình
    val reviews: List<Any>?, // Kiểu dữ liệu cho review chưa rõ, tạm để Any
    val createdByAdminId: Int?, // Có thể không cần hiển thị
    val updatedByAdminId: Int?  // Có thể không cần hiển thị
)