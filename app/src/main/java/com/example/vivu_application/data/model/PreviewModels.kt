package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName // Nếu dùng Gson
// import kotlinx.serialization.Serializable // Nếu dùng Kotlinx Serialization

// @Serializable // Bỏ comment nếu dùng Kotlinx Serialization
data class SubmitReviewRequestBody(
    @SerializedName("targetType")
    val targetType: String, // "TOUR" hoặc "LOCATION"

    @SerializedName("targetId")
    val targetId: Int,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("comment")
    val comment: String
)