package com.example.vivu_application.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vivu_application.data.local.PreferencesManager // Đảm bảo import đúng

class PostControllerFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostController::class.java)) {
            // Gọi constructor của PostController với preferencesManager
            @Suppress("UNCHECKED_CAST") // Bỏ cảnh báo unchecked cast
            return PostController(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

