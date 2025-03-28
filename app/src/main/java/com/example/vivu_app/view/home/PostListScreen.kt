package com.example.vivu_app.view.posts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.vivu_app.model.Post
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.vivu_app.R
import com.example.vivu_app.controller.PostViewModel


@Composable
fun PostListScreen(navController: NavController, viewModel: PostViewModel) {
    val posts by viewModel.posts.collectAsState() // ✅ Lấy danh sách bài viết từ ViewModel


    LazyColumn {
            items(posts) { post ->
                PostItem(post,post.title, post.imageRes, post.rating,post.duration,post.departureDate,post.remainingSeats ,navController)
            }
    }
}





@Composable
fun PostItem(post: Post,title: String, imageRes: Int, rating: Double,duration: String,departureDate: String,remainingSeats: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .width(450.dp)
            .height(170.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp) // Căn giữa và cách viền điện thoại
            .shadow(4.dp, shape = RoundedCornerShape(40.dp)) // Đổ bóng nhẹ
            .clickable { navController.navigate("postDetail/$title") },
        shape = RoundedCornerShape(40.dp), // Bo góc mềm mại
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4DEE1)) // Màu nền nhẹ
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            // 📌 Chồng ảnh & rating lên nhau
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

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart) // 📌 Đưa rating lên góc trên
                        .padding(6.dp)
                        .width(70.dp)
                        .height(20.dp)
                        .background(Color(0xFFF1E8D9), RoundedCornerShape(50.dp)), // 🎨 Nền bo góc
                    contentAlignment = Alignment.Center // ✅ Căn giữa cả chiều ngang & dọc
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // ✅ Căn giữa theo chiều dọc
                        horizontalArrangement = Arrangement.Center, // ✅ Căn giữa theo chiều ngang
                        modifier = Modifier.fillMaxSize() // 🔹 Đảm bảo Row chiếm toàn bộ Box
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star), //  Icon sao
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = post.rating.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(5.dp)) // 🔹 Khoảng cách ảnh & nội dung

            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {

                Spacer(modifier = Modifier.height(25.dp))

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

                // 🗓️ Ngày khởi hành
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

                // 🚍 Số chỗ còn nhận
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

            // ❤️ Nút yêu thích
            Icon(
                painter = painterResource(id = R.drawable.favorite_icon), // Icon trái tim
                contentDescription = "Favorite",
                tint = Color.Black,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
                    .clickable { /* TODO: Xử lý yêu thích */ }
            )
        }
    }
}
