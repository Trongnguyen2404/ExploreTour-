package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

data class Suggestion(
    // Đảm bảo các key khớp với JSON response từ API của bạn
    @SerializedName("type")
    val type: String?,

    @SerializedName("id")
    val id: String?, // Hoặc Long?, Int? tùy thuộc API

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("summary") // Thêm nếu API của bạn có trả về summary
    val summary: String?
)