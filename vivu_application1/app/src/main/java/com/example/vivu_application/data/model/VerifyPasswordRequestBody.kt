package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class cho request body của API xác thực mật khẩu hiện tại.
 * Đảm bảo @SerializedName khớp với key mà backend API yêu cầu.
 */
data class VerifyPasswordRequestBody(
    @SerializedName("currentPassword") // Key backend cần cho mật khẩu hiện tại
    val currentPassword: String
)