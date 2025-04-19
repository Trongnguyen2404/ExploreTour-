package com.example.loginpage.view.chat


import android.util.Log
import androidx.compose.runtime.mutableStateListOf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel: ViewModel() {

    val messageList by lazy {
        mutableStateListOf <MessageModel>()
    }

    val generativeModel : GenerativeModel = GenerativeModel (
        modelName = "gemini-1.5-flash",
        apiKey = Constains.apiKey
    )

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )

                // Lấy thời gian hiện tại
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

                // Thêm tin nhắn của người dùng với thời gian
                messageList.add(MessageModel(
                    message = question,
                    role = "user",
                    time = currentTime
                ))

                // Thêm tin nhắn "Typing..." tạm thời
                messageList.add(MessageModel(
                    message = "Typing....",
                    role = "model",
                    time = currentTime
                ))

                // Gửi tin nhắn và nhận phản hồi từ AI
                val response = chat.sendMessage(question)

                // Xóa tin nhắn "Typing..."
                messageList.removeAt(messageList.lastIndex)

                // Thêm phản hồi của AI với thời gian
                messageList.add(MessageModel(
                    message = response.text.toString(),
                    role = "model",
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) // Thời gian tại thời điểm nhận phản hồi
                ))
            } catch (e: Exception) {
                // Xóa tin nhắn "Typing..." nếu có lỗi
                messageList.removeAt(messageList.lastIndex)

                // Thêm tin nhắn lỗi với thời gian
                messageList.add(MessageModel(
                    message = "Error: " + e.message.toString(),
                    role = "model",
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                ))
            }
        }
    }
}