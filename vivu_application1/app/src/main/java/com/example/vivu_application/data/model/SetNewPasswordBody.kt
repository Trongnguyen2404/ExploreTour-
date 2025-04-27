package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class cho request body của API đặt lại mật khẩu mới.
 * Đảm bảo các @SerializedName khớp với key mà backend API yêu cầu.
 */
data class SetNewPasswordBody(
    @SerializedName("email") // Key backend cần cho email
    val email: String,

    @SerializedName("otp") // Key backend cần cho OTP đã xác thực
    val otp: String, // Giá trị này sẽ đến từ verificationData

    @SerializedName("newPassword") // Key backend cần cho mật khẩu mới
    val newPassword: String,

    @SerializedName("repeatPassword") // Key backend cần cho xác nhận mật khẩu
    val repeatPassword: String
)