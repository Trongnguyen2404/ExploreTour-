package com.example.vivu_app.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vivu_app.data.local.PreferencesManager

class PostControllerFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostController::class.java)) {
            return PostController(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
