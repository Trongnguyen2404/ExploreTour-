package com.example.loginpage.view.chat

data class MessageModel (
    val message : String,
    val role : String,
    val time : String = ""
)