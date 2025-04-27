package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

data class ChatResponseBody(
    // Đảm bảo key "reply" khớp
    @SerializedName("reply")
    val reply: String?,

    // Đảm bảo key "suggestions" khớp và là một danh sách Suggestion
    @SerializedName("suggestions")
    val suggestions: List<Suggestion>?
)