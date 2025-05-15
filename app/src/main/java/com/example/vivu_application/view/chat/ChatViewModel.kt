package com.example.vivu_application.view.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivu_application.data.model.ChatRequestBody
import com.example.vivu_application.data.network.RetrofitClient // Import Retrofit client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel: ViewModel() {

    val messageList by lazy {
        mutableStateListOf <MessageModel>()
    } // Giữ nguyên lazy hoặc bỏ nếu muốn load lại

    // ++ THÊM STATE CHO VIỆC REFRESH ++
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow() // Expose StateFlow để UI quan sát

    private val authApiService = RetrofitClient.authApiService
    private var currentSessionId: String? = null
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
                val requestBody = ChatRequestBody(
                    message = question,
                    sessionId = currentSessionId // Gửi sessionId hiện tại
                )

                // 4. Gọi API Backend (AuthInterceptor sẽ thêm token)
                val response = authApiService.sendChatMessage(requestBody)

                // 5. Xóa tin nhắn "Typing..." (luôn xóa nếu nó đã được thêm)
                if (typingMessageIndex != -1 && typingMessageIndex < messageList.size) {
                    messageList.removeAt(typingMessageIndex)
                }

                // 6. Xử lý phản hồi từ API Backend
                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    chatResponse?.sessionId?.let { // 'it' ở đây là sessionId từ response
                        if (it.isNotBlank()) { // Chỉ cập nhật nếu sessionId từ server không rỗng
                            currentSessionId = it
                        }
                    }
                    val reply = chatResponse?.reply
                    val suggestions = chatResponse?.suggestions // ++ Lấy danh sách suggestions ++

                    if (!reply.isNullOrBlank()) {
                        messageList.add(MessageModel(
                            message = reply,
                            role = "model",
                            time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                            suggestions = chatResponse?.suggestions // Đúng kiểu List<Suggestion>
                        ))
                    } else {
                        messageList.add(MessageModel(
                            message = "Received an empty response.", // Lỗi phản hồi rỗng
                            role = "model",
                            time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        ))
                    }
                } else {
                    // Lỗi từ API (vd: 4xx, 5xx)
                    val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                    messageList.add(MessageModel(
                        message = "Error: ${response.code()} - $errorBody", // Hiển thị lỗi API
                        role = "model",
                        time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    ))
                }

            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi không xác định khác
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

    // ++ THÊM HÀM CLEAR HISTORY ++
    fun clearHistory() {
        // Chỉ thực hiện nếu không đang refresh rồi
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true // Bắt đầu trạng thái refresh
            try {
                val response = authApiService.clearChatHistory()

                if (response.isSuccessful) {
                    // Xóa danh sách tin nhắn ở client SAU KHI API thành công
                    messageList.clear()
                    currentSessionId = null
                } else {
                    // Lỗi từ API khi xóa lịch sử
                    val errorBody = response.errorBody()?.string() ?: "Failed to clear history on server."
                    // Hiển thị lỗi cho người dùng (ví dụ: qua một state riêng hoặc Toast)
                    // Nên có một State<String?> cho lỗi để ChatScreen hiển thị Toast chẳng hạn
                    messageList.add(MessageModel("Error clearing history: $errorBody", "model", SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()))) // Tạm thời thêm vào list
                }
            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi khác
                messageList.add(MessageModel("Network error clearing history.", "model", SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()))) // Tạm thời thêm vào list
            } finally {
                _isRefreshing.value = false // Kết thúc trạng thái refresh
            }
        }
    }
}

