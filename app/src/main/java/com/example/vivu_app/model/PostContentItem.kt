package com.example.vivu_app.model

sealed class PostContentItem {
    data class Text(val content: String) : PostContentItem()
    data class Image(val imageRes: Int? = null, val imageUrl: String? = null) : PostContentItem()
}

