package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class cho request body của API cập nhật thông tin profile.
 * Chỉ chứa các trường người dùng có thể sửa đổi.
 * Đảm bảo @SerializedName khớp với key mà backend API yêu cầu.
 */
data class UpdateProfileRequestBody(
    // Đảm bảo tên key khớp với JSON API yêu cầu
    @SerializedName("fullName")
    val fullName: String?, // Cho phép null nếu người dùng không muốn cập nhật

    @SerializedName("dateOfBirth")
    val dateOfBirth: String?, // Định dạng YYYY-MM-DD nếu API yêu cầu vậy

    @SerializedName("phoneNumber") // Hoặc "mobile" tùy API
    val phoneNumber: String?,

    @SerializedName("profilePictureUrl")
    val profilePictureUrl: String? // Sẽ được cập nhật sau khi upload ảnh thành công
)