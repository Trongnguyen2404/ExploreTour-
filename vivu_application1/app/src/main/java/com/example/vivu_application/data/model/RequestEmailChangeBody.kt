package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class cho request body của API yêu cầu thay đổi email.
 * Đảm bảo các @SerializedName khớp với key mà backend API yêu cầu.
 */
data class RequestEmailChangeBody(
    @SerializedName("currentPassword") // Key backend cần cho mật khẩu hiện tại
    val currentPassword: String,

    @SerializedName("newEmail") // Key backend cần cho email mới
    val newEmail: String
)