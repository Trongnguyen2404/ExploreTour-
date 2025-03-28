package com.example.vivu_app.controller

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.vivu_app.R
import com.example.vivu_app.model.Post

class PostViewModel : ViewModel() {

    // State lưu danh sách bài viết
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // Cập nhật danh sách bài viết theo danh mục
    fun setCategory(category: String) {
        _posts.value = getPostsByCategory(category)
    }

    // Hàm lấy danh sách bài viết theo danh mục
    private fun getPostsByCategory(category: String): List<Post> {
        return when (category) {
            "tour" -> listOf(
                Post(id = 1, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang,
                    rating = 4.5,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
                Post(id = 3, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang,
                    rating = 4.5,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
                Post(id = 2, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau,
                    rating = 4.8,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),

                Post(id = 4, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau,
                    rating = 4.8,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
            )
            "location" -> listOf(
                Post(id = 5, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang,
                    rating = 4.5,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
                Post(id = 6, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau,
                    rating = 4.8,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
                Post(id = 7, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang,
                    rating = 4.5,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
                Post(id = 8, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau,
                    rating = 4.8,
                    duration= "4N3Đ",
                    departureDate= "03/04/2024",
                    remainingSeats= 20
                ),
            )
            else -> emptyList()
        }
    }

    // Hàm lấy bài viết theo tiêu đề
    fun getPostByTitle(postTitle: String): Post? {
        return _posts.value.find { it.title == postTitle }
    }
}
