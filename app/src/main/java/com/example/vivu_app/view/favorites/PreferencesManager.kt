package com.example.vivu_app.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Hàm lưu bài viết yêu thích
    fun saveFavoritePosts(favorites: Set<Int>) {
        sharedPreferences.edit().putStringSet("favorites", favorites.map { it.toString() }.toSet()).apply()
    }

    // Hàm lấy danh sách bài viết yêu thích
    val favoritePosts: StateFlow<Set<Int>> = MutableStateFlow(emptySet()) // Giả lập data
}
