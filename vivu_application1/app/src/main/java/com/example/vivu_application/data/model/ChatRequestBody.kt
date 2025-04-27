package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

data class ChatRequestBody(
    // Đảm bảo key "message" khớp với API backend
    @SerializedName("message")
    val message: String
)
