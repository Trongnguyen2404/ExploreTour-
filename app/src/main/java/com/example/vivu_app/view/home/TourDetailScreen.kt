package com.example.vivu_app.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vivu_app.R
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.ui.components.CombinedExpandableContent
import com.example.vivu_app.ui.components.CommentSection
import com.example.vivu_app.ui.components.ExpandableText


@Composable
fun TourDetailScreen(postId: Int, postViewModel: PostController){
    // Lấy dữ liệu post, return nếu null (giữ nguyên)
    val post = postViewModel.posts.collectAsState().value.find { it.id == postId } ?: return

    // Sử dụng LazyColumn làm thành phần gốc
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // 1. Item thay thế cho padding(top = 50.dp) gốc
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }

        // 2. Item chứa Tiêu đề bài viết
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
                modifier = Modifier
                    .fillParentMaxWidth() // Chiếm hết chiều rộng item
                    .padding(top = 80.dp) // Giữ nguyên padding top 80dp bên trong này
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
                modifier = Modifier
                    .fillParentMaxWidth() // Chiếm hết chiều rộng item
                    .wrapContentSize(Alignment.Center) // Đặt Box vào giữa
                    .width(400.dp) // Giữ nguyên width
                    .height(200.dp) // Giữ nguyên height
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
                            painter = painterResource(id = R.drawable.ic_star), // Thay ID thực tế
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
        }

        // 4. Item chứa Spacer gốc
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 5. Item chứa Text "Thông tin:"
        item {
            Text(text = "Thông tin:",
                style = MaterialTheme.typography.bodyMedium)
        }

        // 6. Item chứa Column các InfoRow
        item {
            Column(modifier = Modifier.fillParentMaxWidth()) { // Thêm fillParentMaxWidth nếu muốn InfoRow chiếm hết
                Spacer(modifier = Modifier.height(10.dp))
                InfoRow(icon = R.drawable.ic_location, text = post.title) // Thay ID icon
                Spacer(modifier = Modifier.height(8.dp)) // Giãn cách
                InfoRow(icon = R.drawable.ic_time, text = "Lịch trình: ${post.duration}") // Thay ID icon
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = R.drawable.ic_calendar, text = "Khởi hành: ${post.departureDate}") // Thay ID icon
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = R.drawable.ic_seat, text = "Số chỗ còn nhận: ${post.remainingSeats}") // Thay ID icon
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = R.drawable.ic_contact, text = "liên hệ: ${post.contact}") // Thay ID icon
                Spacer(modifier = Modifier.height(8.dp))
                // Căn chỉnh Text này nếu cần, ví dụ bằng Modifier.padding(start = ...)
                Text(text = "Mã tour: ${post.tourCode}")
            }
        }

        // 7. Item chứa Spacer gốc
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 8. Item chứa Nội dung mô tả (ExpandableText)
        item {
            CombinedExpandableContent(
                initialContent = post.content,
                detailContents = post.detailContents, // CombinedExpandableContent sẽ xử lý list này
                maxLinesCollapsed = 5 // Hoặc giá trị bạn muốn
            )
        }

        // 9. Item chứa Ảnh lịch trình (chỉ thêm nếu có)
        post.scheduleImageRes?.let { imageRes ->
            item {
                // Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Lịch trình Tour",
                    contentScale = ContentScale.Crop, // Hoặc Fit
                    modifier = Modifier
                        .fillParentMaxWidth() // Chiếm hết chiều rộng item
                        .wrapContentSize(Alignment.Center) // Đặt vào giữa
                        .width(400.dp) // Giữ nguyên width
                        .height(550.dp) // Giữ nguyên height
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }

        // 10. Item chứa Spacer gốc
        item {
            Spacer(modifier = Modifier.height(5.dp))
        }

        // 11. Item chứa Comment Section
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth() // Chiếm hết chiều rộng item
                    .imePadding() // giúp đẩy nội dung khi keyboard hiện lên
            ) {
                // Gọi đến CommentSection và các phần khác
                CommentSection(postId = post.id, postController = postViewModel)
            }
        }
    }
}

