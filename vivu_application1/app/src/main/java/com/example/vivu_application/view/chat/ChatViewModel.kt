package com.example.vivu_application.view.chat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivu_application.data.model.ChatRequestBody
import com.example.vivu_application.data.network.RetrofitClient // Import Retrofit client
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel: ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>() // Giữ nguyên cấu trúc MessageModel của bạn
    }

    // -- XÓA KHỞI TẠO GEMINI MODEL --
    // val generativeModel : GenerativeModel = GenerativeModel(...)

    // Lấy instance của ApiService
    private val authApiService = RetrofitClient.authApiService

    fun sendMessage(question: String) {
        // -- BẮT ĐẦU THAY ĐỔI LOGIC --
        viewModelScope.launch {
            val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            var typingMessageIndex = -1 // Để theo dõi vị trí tin nhắn "Typing..."

            try {
                // 1. Thêm tin nhắn người dùng
                messageList.add(MessageModel(
                    message = question,
                    role = "user", // Giữ nguyên role 'user'
                    time = currentTime
                ))

                // 2. Thêm tin nhắn "Typing..." tạm thời và lấy vị trí của nó
                messageList.add(MessageModel(
                    message = "Thinking...", // Hoặc "Đang trả lời..."
                    role = "model", // Giữ nguyên role 'model'
                    time = currentTime
                ))
                typingMessageIndex = messageList.lastIndex // Lưu lại index của tin nhắn typing

                // 3. Tạo request body cho API backend
                val requestBody = ChatRequestBody(message = question)
                Log.d("ChatViewModel", "Sending chat message request: $requestBody")

                // 4. Gọi API Backend (AuthInterceptor sẽ thêm token)
                val response = authApiService.sendChatMessage(requestBody)

                // 5. Xóa tin nhắn "Typing..." (luôn xóa nếu nó đã được thêm)
                if (typingMessageIndex != -1 && typingMessageIndex < messageList.size) {
                    messageList.removeAt(typingMessageIndex)
                }

                // 6. Xử lý phản hồi từ API Backend
                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    val reply = chatResponse?.reply
                    val suggestions = chatResponse?.suggestions // ++ Lấy danh sách suggestions ++

                    if (!reply.isNullOrBlank()) {
                        // Thêm phản hồi từ backend VÀ suggestions (nếu có)
                        messageList.add(MessageModel(
                            message = reply,
                            role = "model",
                            time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                            suggestions = suggestions // ++ GÁN suggestions vào MessageModel ++
                        ))
                        Log.d("ChatViewModel", "Suggestions attached to message: ${suggestions?.size ?: 0}")
                    } else {
                        Log.e("ChatViewModel", "API call successful but reply is null or blank.")
                        messageList.add(MessageModel(
                            message = "Received an empty response.", // Lỗi phản hồi rỗng
                            role = "model",
                            time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        ))
                    }
                } else {
                    // Lỗi từ API (vd: 4xx, 5xx)
                    val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                    Log.e("ChatViewModel", "API Error: ${response.code()} - $errorBody")
                    messageList.add(MessageModel(
                        message = "Error: ${response.code()} - $errorBody", // Hiển thị lỗi API
                        role = "model",
                        time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    ))
                }

            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi không xác định khác
                Log.e("ChatViewModel", "Network or other exception: ${e.message}", e)
                // Xóa tin nhắn "Typing..." nếu còn tồn tại và có lỗi xảy ra
                if (typingMessageIndex != -1 && typingMessageIndex < messageList.size && messageList[typingMessageIndex].message.contains("Thinking...")) {
                    try { messageList.removeAt(typingMessageIndex) } catch (_: IndexOutOfBoundsException) {}
                }
                messageList.add(MessageModel(
                    message = "Error: Could not connect - ${e.message}", // Lỗi kết nối
                    role = "model",
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                ))
            }
            // -- KẾT THÚC THAY ĐỔI LOGIC --
        }
    }
}

