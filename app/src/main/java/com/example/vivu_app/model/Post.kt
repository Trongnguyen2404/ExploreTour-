package com.example.vivu_app.model

import androidx.compose.runtime.mutableStateListOf


enum class PostType {
    TOUR, LOCATION
}

data class Post(
    val id: Int, // Mỗi Post cần ID duy nhất
    val title: String,
    val content: String, // Thêm nội dung bài viết
    val rating: Double, // Định dạng đúng Double
    val imageRes: Int, // Ảnh từ drawable
    val imageUrl: String? = null, // Link ảnh nếu lấy từ Internet
    val scheduleImageRes: Int? = null, // Hình ảnh lịch trình từ drawable
    val duration: String ? = null, // Thời gian tour
    val departureDate: String ? = null, // Ngày khởi hành
    val remainingSeats: Int ? = null,// Số chỗ còn lại
    val tourCode: String? = null, // Mã tour (chữ + số, ví dụ: "VN1234")
    val contact: String? = null, // Thông tin liên hệ (số điện thoại, email)
    var isFavorite: Boolean,  // Thêm trạng thái yêu thích
    val type: com.example.vivu_app.model.PostType, // Thêm loại bài viết
    val comments: List<Comment> = mutableStateListOf(),

    // Nội dung chi tiết (nhiều ảnh + chữ đan xen)
    val detailContents: List<PostContentItem> = emptyList(),
    // Thông tin riêng cho LOCATION (nullable)
    val description: String? = null
)