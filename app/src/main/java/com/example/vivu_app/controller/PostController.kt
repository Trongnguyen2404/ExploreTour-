package com.example.vivu_app.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.vivu_app.R
import com.example.vivu_app.model.Post
import com.example.vivu_app.preferences.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class PostController(private val preferencesManager: PreferencesManager) : ViewModel() {


    // State lưu danh sách bài viết
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()


    // Cập nhật danh sách bài viết theo danh mục
    fun setCategory(category: String) {
        _posts.value = getPostsByCategory(category).map { post ->
            post.copy(isFavorite = favoritePostIds.value.contains(post.id))
        }
    }

    val favoritePostIds = preferencesManager.favoritePosts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())



    val favoritePosts: StateFlow<List<Post>> = combine(_posts, favoritePostIds) { postsList, favoriteIds ->
        postsList.filter { it.id in favoriteIds }.map { it.copy(isFavorite = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())



    // Hàm toggle favorite: khi bấm tim, cập nhật trạng thái isFavorite của bài viết có id tương ứng
    fun toggleFavorite(postId: Int) {
        viewModelScope.launch {
            val currentFavorites = favoritePostIds.value.toMutableSet()
            if (currentFavorites.contains(postId)) {
                currentFavorites.remove(postId)
            } else {
                currentFavorites.add(postId)
            }
            preferencesManager.saveFavoritePosts(currentFavorites)
        }
    }


    // Hàm lấy danh sách bài viết yêu thích (các bài có isFavorite == true)
    fun getFavoritePosts(): List<Post> {
        return _posts.value.filter { it.isFavorite }
    }

    // Hàm lấy bài viết theo danh mục (giữ nguyên ID)
    private fun getPostsByCategory(category: String): List<Post> {
        return when (category) {
            "tour" -> listOf(
                Post(id = 1, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false),
                Post(id = 3, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false),
                Post(id = 2, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false),
                Post(id = 4, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false)
            )
            "location" -> listOf(
                Post(id = 5, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false),
                Post(id = 6, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false),
                Post(id = 7, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false),
                Post(id = 8, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false)
            )
            else -> emptyList()
        }
    }

    // Hàm lấy bài viết theo tiêu đề
    fun getPostByTitle(postTitle: String): Post? {
        return _posts.value.find { it.title == postTitle }
    }
}
