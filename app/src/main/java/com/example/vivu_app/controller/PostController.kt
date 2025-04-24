package com.example.vivu_app.controller

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivu_app.model.Post
import com.example.vivu_app.model.PostType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.vivu_app.R
import com.example.vivu_app.model.Comment
import com.example.vivu_app.data.local.PreferencesManager
import com.example.vivu_app.model.PostContentItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

        viewModelScope.launch {
            val postsFromCategory = getPostsByCategory(category)

            val updatedPosts = postsFromCategory.map { post ->
                val savedComments = preferencesManager.getComments(post.id)
                post.copy(
                    isFavorite = favoritePostIds.value.contains(post.id),
                    comments = mutableStateListOf<Comment>().apply {
                        addAll(savedComments)
                    }
                )
            }

            _posts.value = updatedPosts
        }
    }

    val favoritePostIds = preferencesManager.favoritePosts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())



//    val favoritePosts: StateFlow<List<Post>> = combine(_posts, favoritePostIds) { postsList, favoriteIds ->
//        postsList.filter { it.id in favoriteIds }.map { it.copy(isFavorite = true) }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favoritePosts: StateFlow<List<Post>> = favoritePostIds.map { favoriteIds ->
        // 1. Lấy TẤT CẢ bài viết từ các category liên quan
        val allPostsData = getPostsByCategory("location") + getPostsByCategory("tour") // Giả sử hàm này trả về dữ liệu gốc

        // 2. Lọc danh sách TẤT CẢ bài viết dựa trên ID yêu thích
        allPostsData
            .filter { post -> post.id in favoriteIds }
            .map { favoritePost ->
                // 3. Đảm bảo trạng thái isFavorite là true và có thể load thêm dữ liệu cần thiết (như comments)
                val savedComments = preferencesManager.getComments(favoritePost.id) // Load comments cho bài yêu thích
                favoritePost.copy(
                    isFavorite = true, // Chắc chắn là true vì đã lọc theo favoriteIds
                    comments = mutableStateListOf<Comment>().apply { addAll(savedComments) }
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Hàm toggle favorite: khi bấm tim, cập nhật trạng thái isFavorite của bài viết có id tương ứng
    fun toggleFavorite(postId: Int) {
        viewModelScope.launch {
            // 1. Lấy danh sách ID yêu thích hiện tại
            val currentFavorites = favoritePostIds.value.toMutableSet()

            // 2. Kiểm tra xem bài viết này CÓ đang là yêu thích KHÔNG (trước khi thay đổi)
            val isCurrentlyFavorite = currentFavorites.contains(postId)

            // 3. Thêm hoặc xóa khỏi danh sách yêu thích
            if (isCurrentlyFavorite) {
                currentFavorites.remove(postId)
            } else {
                currentFavorites.add(postId)
            }

            // 4. Lưu trạng thái yêu thích mới vào Preferences
            preferencesManager.saveFavoritePosts(currentFavorites)

            // 5. Cập nhật trạng thái isFavorite trong _posts (danh sách hiện tại đang hiển thị)
            //    để UI (icon trái tim) cập nhật ngay lập tức nếu bài viết đó đang hiển thị.
            //    Sử dụng `update` để đảm bảo tính nhất quán.
            _posts.update { currentPostsList ->
                currentPostsList.map { post ->
                    if (post.id == postId) {
                        // Trạng thái isFavorite mới sẽ là ngược lại của trạng thái cũ (!isCurrentlyFavorite)
                        post.copy(isFavorite = !isCurrentlyFavorite)
                    } else {
                        post // Giữ nguyên các bài viết khác
                    }
                }
            }
        }
    }





    // Hàm lấy danh sách bài viết yêu thích (các bài có isFavorite == true)
    fun getFavoritePosts(): List<Post> {
        return _posts.value.filter { it.isFavorite }
    }
    fun setMultipleCategories(categories: List<String>) {
        viewModelScope.launch {
            val combinedPosts = categories.flatMap { category ->
                getPostsByCategory(category)
            }.map { post ->
                val savedComments = preferencesManager.getComments(post.id)
                post.copy(
                    isFavorite = favoritePostIds.value.contains(post.id),
                    comments = mutableStateListOf<Comment>().apply {
                        addAll(savedComments)
                    }
                )
            }

            _posts.value = combinedPosts
        }
    }


    // Hàm lấy bài viết theo tiêu đề
    fun getPostByTitle(postTitle: String): Post? {
        return _posts.value.find { it.title == postTitle }
    }

    //phần bình luận
    private val _comments = mutableStateListOf<Comment>()
    val comments: List<Comment> get() = _comments

    fun addComment(postId: Int, comment: Comment) {
        viewModelScope.launch {
            _posts.value = _posts.value.map { post ->
                if (post.id == postId) {
                    val updatedComments = post.comments + comment
                    preferencesManager.saveComments(postId, updatedComments)
                    post.copy(comments = updatedComments)
                } else post
            }
        }
    }



    init {
        setMultipleCategories(listOf("location", "tour")) // Gọi cả 2!
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
                    type = PostType.TOUR,
                    comments = mutableStateListOf()
                ),

                Post(
                    id = 3, title = "Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "BN5678", contact = "0346019375", type = PostType.TOUR),
                Post(
                    id = 2, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "VN9876", contact = "0346019375", type = PostType.TOUR),
                Post(
                    id = 4, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "QT2409", contact = "0988434280", type = PostType.TOUR)
            )
            "location" -> listOf(
                Post(
                    id = 5, title = "Tour Hà Nội",
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
                    rating = 4.5, duration = "4N3Đ",
                    isFavorite = false,
                    description = "Kinh nghiệm du lịch Vũng Tàu: Ăn gì? Chơi gì? Ở đâu?",
                    type = PostType.LOCATION,
                    comments = mutableStateListOf()
                ),
                Post(
                    id = 6, title = "testSài Gòn",
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
                    imageRes = R.drawable.vung_tau,
                    rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024",
                    remainingSeats = 20,
                    isFavorite = false,
                    tourCode = "HN2500",
                    contact = "0925144923",
                    type = PostType.LOCATION,
                    comments = mutableStateListOf(),
                    detailContents = listOf(
                        PostContentItem.Text("Bắt đầu từ phố cổ..."),
                        PostContentItem.Image(imageRes = R.drawable.vung_tau),
                        PostContentItem.Text("Tiếp theo là Văn Miếu..."),
                        PostContentItem.Image(imageRes = R.drawable.vung_tau)
                    )
                ),
                Post(
                    id = 7, title = "Tour Hà Nội",
                    content = "Khám phá Hà Nội",
                    imageRes = R.drawable.nha_trang, rating = 4.5, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "GJ12355", contact = "0123456789", type = PostType.LOCATION),
                Post(
                    id = 8, title = "Tour Sài Gòn",
                    content = "Trải nghiệm Sài Gòn",
                    imageRes = R.drawable.vung_tau, rating = 4.8, duration = "4N3Đ",
                    departureDate = "03/04/2024", remainingSeats = 20, isFavorite = false, tourCode = "ON5678", contact = "0346019375", type = PostType.LOCATION)
            )
            else -> emptyList()
        }
    }




}