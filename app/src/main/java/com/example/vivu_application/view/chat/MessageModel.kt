package com.example.vivu_application.view.chat

import com.example.vivu_application.data.model.Suggestion 

data class MessageModel(
    val message: String,
    val role: String, // "user" hoặc "model"
    val time: String,
    val suggestions: List<Suggestion>? = null // Danh sách gợi ý, mặc định là null


)