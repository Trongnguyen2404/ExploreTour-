package com.example.vivu_app.model

data class Post(
    val id: Int, // Mỗi Post cần ID duy nhất
    val title: String,
    val content: String, // 🔹 Thêm nội dung bài viết
    val rating: Double, // 🔹 Định dạng đúng Double
    val imageRes: Int, // 🔹 Ảnh từ drawable
    val imageUrl: String? = null, // 🔹 Link ảnh nếu lấy từ Internet
    val duration: String = "", // 🔹 Thời gian tour
    val departureDate: String = "", // 🔹 Ngày khởi hành
    val remainingSeats: Int = 0 // 🔹 Số chỗ còn lại
)
