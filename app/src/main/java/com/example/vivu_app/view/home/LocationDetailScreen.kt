package com.example.vivu_app.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vivu_app.R
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.ui.components.CombinedExpandableContent
import com.example.vivu_app.ui.components.CommentSection


@Composable
fun LocationDetailScreen(postId: Int, postViewModel: PostController) {
    // Lấy dữ liệu post, return nếu null (giữ nguyên)
    val post = postViewModel.posts.collectAsState().value.find { it.id == postId } ?: return

    // Sử dụng LazyColumn làm thành phần gốc
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Áp dụng padding gốc thành contentPadding
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        // Không cần verticalArrangement nếu dùng Spacer rõ ràng
    ) {
        // 1. Item thay thế cho padding(top = 150.dp)
        item {
            Spacer(modifier = Modifier.height(130.dp))
        }

        // 2. Item chứa Tiêu đề bài viết
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = "CHÀO MỪNG ĐẾN",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp, // Đặt kích thước 20sp
                        fontWeight = FontWeight.Bold // In đậm
                    ),
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
        }

        // 3. Item chứa Ảnh và Rating
        item {
            Box(
                // Căn giữa Box này trong LazyColumn item nếu cần
                modifier = Modifier
                    .fillMaxWidth() // Chiếm hết chiều rộng của LazyColumn item
                    .wrapContentSize(Alignment.Center) // Đặt Box vào giữa nếu nó nhỏ hơn
                    .width(400.dp) // Giữ nguyên width gốc
                    .height(200.dp)
                    .clip(RoundedCornerShape(40.dp))
            ) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Rating (giữ nguyên bên trong Box)
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
                            // Thay R.drawable.ic_star bằng ID thực tế của bạn
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            tint = Color.Unspecified, // Hoặc màu bạn muốn
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
        }

        // 4. Item chứa Spacer gốc
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 5. Item chứa Nội dung kết hợp (Expandable)
        item {
            CombinedExpandableContent(
                initialContent = post.content,
                detailContents = post.detailContents, // CombinedExpandableContent sẽ xử lý list này
                maxLinesCollapsed = 5 // Hoặc giá trị bạn muốn
            )
        }

        // 6. Item chứa Spacer gốc
        item {
            Spacer(modifier = Modifier.height(16.dp)) // Spacer trước ảnh lịch trình
        }


        // 7. Item chứa Ảnh lịch trình (chỉ thêm nếu có)
        post.scheduleImageRes?.let { imageRes ->
            item {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Lịch trình Tour",
                    contentScale = ContentScale.Crop, // Hoặc ContentScale.Fit tùy bạn muốn
                    modifier = Modifier
                        .fillParentMaxWidth() // Chiếm hết chiều rộng LazyColumn
                        .wrapContentSize(Alignment.Center) // Đặt vào giữa
                        .width(400.dp) // Giữ nguyên width
                        .height(550.dp) // Giữ nguyên height
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }

        // 8. Item chứa Spacer gốc
        item {
            Spacer(modifier = Modifier.height(5.dp))
        }

        // 9. Item chứa Comment Section
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth() // Chiếm hết chiều rộng LazyColumn
                    .imePadding() // giúp đẩy nội dung khi keyboard hiện lên
            ) {
                // Gọi đến CommentSection và các phần khác
                CommentSection(postId = post.id, postController = postViewModel)
            }
        }
    }
}
