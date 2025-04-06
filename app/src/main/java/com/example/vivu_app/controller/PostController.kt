package com.example.vivu_app.controller

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.vivu_app.R
import com.example.vivu_app.model.Comment
import com.example.vivu_app.model.Post
import com.example.vivu_app.model.PostType
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
                Post(
                    id = 1,
                    title = "NHA TRANG",
                    content = """
        Du lịch Nha Trang 2025 cùng Du lịch Việt, chúng tôi luôn tổ chức Tour Du Lịch Nha Trang 2025, 
        những Tour Nha Trang 2025 chất lượng, giá rẻ để phục vụ khách du lịch trên toàn quốc.  
        
        Du lịch Nha Trang - Thành phố biển Nha Trang nổi tiếng với những cảnh quan thiên nhiên đẹp 
        “mê hoặc” lòng người, hàng năm thu hút hàng trăm ngàn du khách cả trong và ngoài nước đến tham quan nghỉ dưỡng.  
        
        Nếu bạn đang tìm kiếm một chuyến du lịch đúng nghĩa nghỉ dưỡng thì Tour du lịch Nha Trang là sự lựa chọn 
        tuyệt vời dành cho bạn. Đến với Thành phố biển Nha Trang bạn sẽ được tham quan ngắm cảnh với rất 
        nhiều những danh lam thắng cảnh nổi tiếng, được thử trải nghiệm câu tôm trên thuyền khi mặt trời đã 
        ngả bóng... Được thưởng thức nhiều món ăn hấp dẫn, cùng khí hậu mát mẻ... Hứa hẹn đây sẽ là một kỳ 
        nghỉ đầy thú vị và ý nghĩa dành cho bạn.
    """.trimIndent(),
                    imageRes = R.drawable.nha_trang,
                    rating = 4.5,
                    duration = "4N3Đ",
                    departureDate = "03/04/2024",
                    remainingSeats = 20,
                    isFavorite = false,
                    tourCode = "VN1234",
                    contact = "0346019375",
                    scheduleImageRes = R.drawable.calendar_photo,
                    type = PostType.TOUR
                ),

                        Post(id = 3, title = "Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "BN5678", contact = "0346019375", type = PostType.TOUR),
                Post(id = 2, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "VN9876", contact = "0346019375", type = PostType.TOUR),
                Post(id = 4, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "QT2409", contact = "0988434280", type = PostType.TOUR)
            )
            "location" -> listOf(
                Post(id = 5, title = "Tour Hà Nội",
                    content = """
        Du lịch Nha Trang 2025 cùng Du lịch Việt, chúng tôi luôn tổ chức Tour Du Lịch Nha Trang 2025, 
        những Tour Nha Trang 2025 chất lượng, giá rẻ để phục vụ khách du lịch trên toàn quốc.  
        
        Du lịch Nha Trang - Thành phố biển Nha Trang nổi tiếng với những cảnh quan thiên nhiên đẹp 
        “mê hoặc” lòng người, hàng năm thu hút hàng trăm ngàn du khách cả trong và ngoài nước đến tham quan nghỉ dưỡng.  
        
        Nếu bạn đang tìm kiếm một chuyến du lịch đúng nghĩa nghỉ dưỡng thì Tour du lịch Nha Trang là sự lựa chọn 
        tuyệt vời dành cho bạn. Đến với Thành phố biển Nha Trang bạn sẽ được tham quan ngắm cảnh với rất 
        nhiều những danh lam thắng cảnh nổi tiếng, được thử trải nghiệm câu tôm trên thuyền khi mặt trời đã 
        ngả bóng... Được thưởng thức nhiều món ăn hấp dẫn, cùng khí hậu mát mẻ... Hứa hẹn đây sẽ là một kỳ 
        nghỉ đầy thú vị và ý nghĩa dành cho bạn.
    """.trimIndent(),
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "FG2044", contact = "0346019375", type = PostType.LOCATION),
                Post(id = 6, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "HN2500", contact = "0925144923", type = PostType.LOCATION),
                Post(id = 7, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "GJ12355", contact = "0123456789", type = PostType.LOCATION),
                Post(id = 8, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "ON5678", contact = "0346019375", type = PostType.LOCATION)
            )
            else -> emptyList()
        }
    }

    // Hàm lấy bài viết theo tiêu đề
    fun getPostByTitle(postTitle: String): Post? {
        return _posts.value.find { it.title == postTitle }
    }

//phần bình luận
    private val _comments = mutableStateListOf<Comment>()
    val comments: List<Comment> get() = _comments

    fun addComment(comment: Comment) {
        _comments.add(comment)
        // Ở đây bạn có thể thêm logic gửi comment lên server nếu cần
    }
}