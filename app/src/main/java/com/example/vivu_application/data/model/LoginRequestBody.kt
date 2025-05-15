package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho cấu trúc dữ liệu được gửi đi trong body
 * của yêu cầu API đăng nhập.
 *
 * Các tên trường (`emailOrUsername`, `password`) cần khớp với các key mà API backend
 * mong đợi trong JSON request body. Sử dụng @SerializedName nếu cần thiết.
 */
data class LoginRequestBody(

    // Trường này chứa email HOẶC username người dùng nhập vào.
    // API backend của bạn cần xử lý việc nhận diện đây là email hay username.
    // QUAN TRỌNG: Thay đổi giá trị trong @SerializedName nếu API của bạn mong đợi
    // một key khác (ví dụ: "username", "loginId", "email").
    @SerializedName("usernameOrEmail")
    val emailOrUsername: String,

    // Mật khẩu người dùng nhập.
    // QUAN TRỌNG: Thay đổi giá trị trong @SerializedName nếu API của bạn mong đợi
    // một key khác (ví dụ: "pass", "secret").
    @SerializedName("password")
    val password: String
)