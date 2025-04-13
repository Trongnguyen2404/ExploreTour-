package com.example.loginpage.model

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
    val duration: String = "", // Thời gian tour
    val departureDate: String = "", // Ngày khởi hành
    val remainingSeats: Int = 0 ,// Số chỗ còn lại
    val tourCode: String, // Mã tour (chữ + số, ví dụ: "VN1234")
    val contact: String, // Thông tin liên hệ (số điện thoại, email)
    var isFavorite: Boolean,  // Thêm trạng thái yêu thích
    val type: PostType // Thêm loại bài viết
)