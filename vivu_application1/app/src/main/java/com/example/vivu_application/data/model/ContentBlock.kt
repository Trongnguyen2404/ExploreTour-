package com.example.vivu_application.data.model

// Đại diện cho một khối nội dung trong chi tiết Location
data class ContentBlock(
    val id: Int,
    val blockType: String, // "TEXT", "IMAGE"
    val contentValue: String, // Nội dung text (có thể là HTML) hoặc URL ảnh
    val caption: String?, // Chú thích cho ảnh
    val orderIndex: Int // Thứ tự hiển thị
)