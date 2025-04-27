package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class cho request body của API xác nhận thay đổi email bằng OTP.
 * Đảm bảo các @SerializedName khớp với key mà backend API yêu cầu.
 */
data class VerifyEmailChangeBody(
    @SerializedName("newEmail") // Key backend cần cho email mới
    val newEmail: String,

    @SerializedName("otp") // Key backend cần cho OTP
    val otp: String
)