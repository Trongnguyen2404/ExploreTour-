package com.example.vivu_application.data.model // Đảm bảo đúng package

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho cấu trúc JSON được trả về từ API đăng nhập,
 * bao gồm cả trường hợp thành công và thất bại.
 * Các @SerializedName phải khớp chính xác với tên key trong JSON response.
 */
data class LoginResponseBody(

    // === Trường hợp THÀNH CÔNG ===
    @SerializedName("token")
    val accessToken: String?,

    @SerializedName("type")
    val tokenType: String?,

    @SerializedName("refreshToken")
    val refreshToken: String?,

    @SerializedName("id")
    val id: Long?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("roles")
    val roles: List<String>?,

    // === Trường hợp THẤT BẠI (Thêm vào) ===
    @SerializedName("success")
    val success: Boolean?, // Sẽ là false khi lỗi, null hoặc true khi thành công (tùy API)

    @SerializedName("message")
    val message: String?, // Chứa thông báo lỗi khi thất bại

    // === Trường data (Thêm vào nếu cấu trúc lỗi có) ===
    // Có thể giữ lại hoặc bỏ đi tùy thuộc bạn có cần xử lý 'data' khi lỗi không
    @SerializedName("data")
    val data: Any? // Thường là null khi lỗi theo ví dụ của bạn
)