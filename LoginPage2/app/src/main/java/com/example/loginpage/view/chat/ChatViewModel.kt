package com.example.loginpage.view.chat


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    val messageList by lazy {
        mutableStateListOf <MessageModel>()
    }

    val generativeModel : GenerativeModel = GenerativeModel (
        modelName = "gemini-pro",
        apiKey = Constains.apiKey
    )

    fun sendMessage(question : String) {
        viewModelScope.launch {
            val chat = generativeModel.startChat()

            messageList.add(MessageModel(question, "user"))

            val response = chat.sendMessage(question)

            messageList.add(MessageModel(response.text.toString(), "user"))

        }

    }

}