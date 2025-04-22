package com.example.vivu_app.view.chat

data class MessageModel (
    val message : String,
    val role : String,
    val time : String = ""
)