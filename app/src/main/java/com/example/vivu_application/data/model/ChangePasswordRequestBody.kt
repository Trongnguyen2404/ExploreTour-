package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class cho request body của API thay đổi mật khẩu sau khi đăng nhập.
 * Đảm bảo các @SerializedName khớp với key mà backend API yêu cầu.
 */
data class ChangePasswordRequestBody(
    @SerializedName("currentPassword") // Key backend cần cho mật khẩu hiện tại
    val currentPassword: String,

    @SerializedName("newPassword") // Key backend cần cho mật khẩu mới
    val newPassword: String,

    // Key backend cần cho xác nhận mật khẩu mới (Tên key có thể khác)
    @SerializedName("repeatNewPassword") // Hoặc "confirmPassword", "passwordConfirmation",...
    val repeatNewPassword: String
)