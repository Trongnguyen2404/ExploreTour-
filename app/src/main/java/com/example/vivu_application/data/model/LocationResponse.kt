package com.example.vivu_application.data.model

// Cấu trúc response tổng thể từ API locations (giống TourResponse về cấu trúc phân trang)
data class LocationResponse(
    val content: List<Location>,
    val pageNo: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)