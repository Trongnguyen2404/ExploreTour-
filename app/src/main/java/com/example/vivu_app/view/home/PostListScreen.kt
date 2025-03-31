package com.example.vivu_app.view.posts

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
<<<<<<< HEAD
fun PostListScreen(navController: NavController, viewModel: PostViewModel) {
    val posts by viewModel.posts.collectAsState() // Lấy danh sách bài viết từ ViewModel
=======
fun PostListScreen(navController: NavController, postController: PostController) {
    val posts by postController.posts.collectAsState(initial = emptyList()) // Lấy dữ liệu mới
>>>>>>> 33a34e0 (Update new code)

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
    post: Post, // Sửa type từ Int thành Post
    navController: NavController,
    postController: PostController,
    onFavoriteClick: () -> Unit
) {
    // Lấy danh sách bài viết yêu thích từ ViewModel
    val favoritePosts = postController.favoritePosts.collectAsState(initial = emptyList<Post>()).value


    // Kiểm tra nếu bài viết có trong danh sách yêu thích (isFavorite = true)
    val isFavorited = favoritePosts.any { it.id == post.id }
    Card(
        modifier = Modifier
            .width(450.dp)
            .height(170.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate("postDetail/${post.title}") },
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4DEE1))
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
<<<<<<< HEAD
            // Chồng ảnh & rating lên nhau
=======
>>>>>>> 33a34e0 (Update new code)
            Box(
                modifier = Modifier
                    .width(155.dp)
                    .height(140.dp)
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
<<<<<<< HEAD
                        .align(Alignment.TopStart) // Đưa rating lên góc trên
                        .padding(6.dp)
                        .width(70.dp)
                        .height(20.dp)
                        .background(Color(0xFFF1E8D9), RoundedCornerShape(50.dp)), // Nền bo góc
                    contentAlignment = Alignment.Center // Căn giữa cả chiều ngang & dọc
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Căn giữa theo chiều dọc
                        horizontalArrangement = Arrangement.Center, // Căn giữa theo chiều ngang
                        modifier = Modifier.fillMaxSize() // Đảm bảo Row chiếm toàn bộ Box
=======
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
>>>>>>> 33a34e0 (Update new code)
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

<<<<<<< HEAD
            Spacer(modifier = Modifier.width(5.dp)) //  Khoảng cách ảnh & nội dung

            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
=======
            Spacer(modifier = Modifier.width(5.dp))
>>>>>>> 33a34e0 (Update new code)

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(25.dp))
<<<<<<< HEAD

                // tên địa điểm
                Row(verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location), // Icon vị trí
                        contentDescription = null,
                        modifier = Modifier.width(15.dp)
                            .height(15.dp)
                    )
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                //lịch trình
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time), // Icon time
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = " lịch trình: $duration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 🗓 Ngày khởi hành
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar), // Icon lịch
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = " Khởi hành: $departureDate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                //  Số chỗ còn nhận
                Row(verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_seat), // Icon ghế
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = " Số chỗ còn nhận: $remainingSeats",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            }

            //  Nút yêu thích
=======
                InfoRow(icon = R.drawable.ic_location, text = post.title)
                InfoRow(icon = R.drawable.ic_time, text = "Lịch trình: ${post.duration}")
                InfoRow(icon = R.drawable.ic_calendar, text = "Khởi hành: ${post.departureDate}")
                InfoRow(icon = R.drawable.ic_seat, text = "Số chỗ còn nhận: ${post.remainingSeats}")
            }

            // Nút yêu thích
>>>>>>> 33a34e0 (Update new code)
            Icon(
                painter = painterResource(
                    id = if (isFavorited) R.drawable.favorite_icon1 else R.drawable.favorite_icon
                ),
                contentDescription = "Favorite",
                tint = if (isFavorited) Color.Red else Color.Gray,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
                    .clickable {
                        onFavoriteClick()
                    }
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
