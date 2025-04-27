package com.example.vivu_application.view.chat

import com.example.vivu_application.data.model.Suggestion // ++ IMPORT Suggestion ++

data class MessageModel(
    val message: String,
    val role: String, // "user" hoặc "model"
    val time: String,
    // ++ THÊM TRƯỜNG NÀY ++
    val suggestions: List<Suggestion>? = null // Danh sách gợi ý, mặc định là null
)