// file: com.example.vivu_application.data.model.ChatResponseBody.kt
package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

data class ChatResponseBody(
    @SerializedName("reply")
    val reply: String?,

    @SerializedName("suggestions")
    val suggestions: List<Suggestion>?,

    // ++ THÊM TRƯỜNG NÀY ++
    @SerializedName("sessionId")
    val sessionId: String? // Server sẽ trả về sessionId
)