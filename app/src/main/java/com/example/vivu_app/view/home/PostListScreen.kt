package com.example.vivu_app.view.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
    postController: PostController,
    onFavoriteClick: () -> Unit
)
{
    // Lấy danh sách bài viết yêu thích từ ViewModel
    val favoritePostsIds by postController.favoritePostIds.collectAsState()

    // Kiểm tra nếu bài viết có trong danh sách yêu thích (isFavorite = true)
    val isFavorited = favoritePostsIds.contains(post.id)
    Card(
        modifier = Modifier
            .width(450.dp)
            .height(170.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate("postDetail/${post.title}") },
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4DEE1))
    ) {
        Row(modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically // thêm cái này!
        ) {
            //ảnh v rating nằm chung
            Box(
                modifier = Modifier
                    .width(155.dp)
                    .fillMaxHeight() // thêm cái này cho khớp chiều cao cha
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Rating
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .width(70.dp)
                        .height(20.dp)
                        .background(Color(0xFFF1E8D9), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
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
            }

            Spacer(modifier = Modifier.width(5.dp))

            Column(modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), // 👈 giúp Column luôn đầy chiều cao
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
//                Spacer(modifier = Modifier.height(25.dp))
                InfoRow(icon = R.drawable.ic_location, text = post.title)
                InfoRow(icon = R.drawable.ic_time, text = "Lịch trình: ${post.duration}")
                InfoRow(icon = R.drawable.ic_calendar, text = "Khởi hành: ${post.departureDate}")
                InfoRow(icon = R.drawable.ic_seat, text = "Số chỗ còn nhận: ${post.remainingSeats}")
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