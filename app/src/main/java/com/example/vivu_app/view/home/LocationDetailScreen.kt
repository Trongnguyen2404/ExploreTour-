package com.example.vivu_app.view.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_app.R
import com.example.vivu_app.model.Post
import com.example.vivu_app.view.posts.InfoRow


@Composable
fun LocationDetailScreen(post: Post, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 150.dp) // Đẩy nội dung xuống
            .verticalScroll(rememberScrollState()) // Cuộn nếu nội dung dài
    ) {

        // Tiêu đề bài viết
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "CHÀO MỪNG ĐẾN",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,// Đặt kích thước 20sp
                    fontWeight = FontWeight.Bold ),// In đậm
                textAlign = TextAlign.Center
            )

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }

        //ảnh và rating chung hình
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(40.dp))
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
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
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

        Spacer(modifier = Modifier.height(8.dp))


        // Nội dung mô tả bài viết
        ExpandableText(text = post.content)


        //lịch tour nếu có
        post.scheduleImageRes?.let { imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Lịch trình Tour",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(400.dp)
                    .height(550.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }




        Spacer(modifier = Modifier.height(24.dp))

        // Hàng chứa nút quay lại và yêu thích
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại")
            }

            var isFavorite by remember { mutableStateOf(post.isFavorite) }
            IconButton(onClick = { isFavorite = !isFavorite }) {
                Icon(
                    painter = painterResource(id = if (isFavorite) R.drawable.favorite_icon1 else R.drawable.favorite_icon),
                    contentDescription = "Yêu thích",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(50.dp)
                )
            }
        }
    }
}

@Composable
fun ExpandableText(text: String, maxLines: Int = 3) {
    var expanded by remember { mutableStateOf(false) } // Trạng thái mở rộng hoặc thu gọn

    Column {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines, // Hiển thị đầy đủ khi mở rộng
            overflow = TextOverflow.Ellipsis, // Hiển thị "..." nếu bị cắt
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách nhỏ

        Text(
            text = if (expanded) "Thu gọn" else "Xem thêm...",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { expanded = !expanded } // Nhấn để thay đổi trạng thái
                .padding(top = 4.dp)
        )
    }
}
