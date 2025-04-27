package com.example.vivu_application.data.model

// Dữ liệu cho một tour từ API
data class Tour(
    val id: Int,
    val title: String,
    val mainImageUrl: String?, // URL ảnh
    val locationName: String?, // Tên địa điểm (có thể dùng cho title hoặc location)
    val itineraryDuration: String?, // Thời gian tour (3N2Đ)
    val departureDate: String?, // Ngày khởi hành
    val availableSlots: Int, // Số chỗ còn
    val averageRating: Double // Rating
)