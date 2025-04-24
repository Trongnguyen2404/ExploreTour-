package com.example.vivu_app.view.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.vivu_app.R
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.model.Post
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import com.example.vivu_app.model.PostType


@Composable
fun PostListScreen(navController: NavController, postController: PostController,modifier: Modifier = Modifier) {
    val posts by postController.posts.collectAsState(initial = emptyList()) // Lấy dữ liệu mới

    Log.d("PostListScreen", "Received posts: $posts") // Kiểm tra dữ liệu


    LazyColumn {
        items(posts) { post ->
            PostItem(
                post = post,
                navController = navController,
                postController = postController,
                onFavoriteClick = {
                    postController.toggleFavorite(post.id)
                }
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    navController: NavController,
    postController: PostController, // Giữ lại nếu cần cho favorite hoặc logic khác
    onFavoriteClick: () -> Unit
) {
    // Lấy danh sách bài viết yêu thích từ ViewModel
    val favoritePostsIds by postController.favoritePostIds.collectAsState()

    // Kiểm tra nếu bài viết có trong danh sách yêu thích (isFavorite = true)
    val isFavorited = favoritePostsIds.contains(post.id)

    var isClicked by remember { mutableStateOf(false) }

    val cardBackgroundBrush = if (isClicked) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFEFB0C9), // màu hồng
                Color(0xFFB9D6F3)  // màu xanh nhạt
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE4DEE1), // màu xám nhạt mặc định
                Color(0xFFE4DEE1)
            )
        )
    }

    Card(
        modifier = Modifier
            .width(450.dp)
            .height(170.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color(0xFFFFA726))
            ) {
                isClicked = !isClicked
                navController.navigate("postDetail/${post.title}")
            }
            .background(brush = cardBackgroundBrush, shape = RoundedCornerShape(40.dp)),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ){
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Phần Ảnh và Rating (Giữ nguyên cấu trúc Box) ---
            Box(
                modifier = Modifier
                    .width(155.dp) // Điều chỉnh chiều rộng ảnh nếu cần
                    .fillMaxHeight()// Điều chỉnh chiều cao ảnh nếu cần (hoặc fillMaxHeight nếu muốn bằng Card)
                    .clip(RoundedCornerShape(16.dp)) // Bo góc ảnh nhỏ hơn
            ) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = post.title, // Mô tả ảnh tốt hơn
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Rating Box
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        // .width(70.dp) // Để tự động co giãn theo nội dung
                        .wrapContentWidth()
                        .height(20.dp)
                        .background(Color(0xFFF1E8D9), RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp), // Thêm padding ngang cho text bên trong
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        // horizontalArrangement = Arrangement.Center, // Không cần vì đã wrap content
                        // modifier = Modifier.fillMaxSize() // Không cần fill size
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            tint = Color.Unspecified, // Giữ màu gốc của icon
                            modifier = Modifier.size(16.dp) // Kích thước icon nhỏ hơn
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = post.rating.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
                // Optional: Thêm Text Overlay cho Location như "DU LỊCH VŨNG TÀU"
                // if (post.type == PostType.LOCATION) { ... }
            }

            Spacer(modifier = Modifier.width(5.dp)) // Tăng khoảng cách

            // --- Phần Nội dung Text (Conditional) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), //  giúp Column luôn đầy chiều cao
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                when (post.type) {
                    PostType.TOUR -> {
                        // Hiển thị các InfoRow
                        InfoRow(icon = R.drawable.ic_location, text = post.title)
                        Spacer(modifier = Modifier.height(2.dp))
                        InfoRow(icon = R.drawable.ic_time, text = "Lịch trình: ${post.duration}")
                        Spacer(modifier = Modifier.height(2.dp))
                        InfoRow(icon = R.drawable.ic_calendar, text = "Khởi hành: ${post.departureDate}")
                        Spacer(modifier = Modifier.height(2.dp))
                        InfoRow(icon = R.drawable.ic_seat, text = "Số chỗ còn nhận: ${post.remainingSeats}")
                    }
                    PostType.LOCATION -> {
                        // Hiển thị Title (có thể là tên địa điểm ngắn gọn)
                        // Hiển thị Description (nội dung dài)
                        post.description?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it, // Ví dụ: "Kinh nghiệm du lịch Vũng Tàu: Ăn gì? Chơi gì? Ở đâu?"
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                maxLines = 4, // Giới hạn số dòng mô tả
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp // Điều chỉnh khoảng cách dòng nếu cần
                            )
                        }
                    }
                    else -> { // Trường hợp không xác định
                        Text(
                            text = post.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text("Loại bài viết không xác định", color = Color.Red)
                    }
                }
            }

            // Nút yêu thích
            Icon(
                painter = painterResource(
                    id = if (isFavorited) R.drawable.favorite_icon1 else R.drawable.favorite_icon
                ),
                contentDescription = if (isFavorited) "Hủy yêu thích" else "Thêm vào yêu thích",
                tint = if (isFavorited) Color.Red else Color.Gray,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .align(Alignment.Top) // cố định icon ở góc trên
                    .size(24.dp)
                    .clickable { onFavoriteClick() }
            )
        }
    }
}


@Composable
fun InfoRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}