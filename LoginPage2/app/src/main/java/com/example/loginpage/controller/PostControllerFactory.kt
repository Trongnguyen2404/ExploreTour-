package com.example.loginpage.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.loginpage.view.favorites.PreferencesManager

class PostControllerFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostController::class.java)) {
            return PostController(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
