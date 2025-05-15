package com.example.vivu_application.view.home

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
// Import Model chi tiết mới
import com.example.vivu_application.data.model.LocationDetail

import com.example.vivu_application.viewmodel.HomeViewModel
import com.example.vivu_application.ui.components.ReviewSection
import com.example.vivu_application.R
// Import các composable phụ trợ nếu có
// import com.example.loginpage.ui.components.CombinedExpandableContent // Có thể không cần cho Location
// Import InfoRow nếu cần
// import com.example.loginpage.view.home.InfoRow


// Đổi tên và sửa tham số
@Composable
fun LocationDetailScreenContent(locationDetail: LocationDetail) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // 1. Spacer giữ khoảng cách từ top
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }

        // 2. Tiêu đề bài viết
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillParentMaxWidth().padding(top = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "EXPLORE THE LOCATION", // Tiêu đề chung
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                // Lấy title từ locationDetail
                Text(
                    text = locationDetail.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // 3. Ảnh chính (Header Image) và Rating
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .aspectRatio(16f / 9f)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                // Sử dụng AsyncImage cho headerImageUrl
                AsyncImage(
                    model = locationDetail.headerImageUrl, // Lấy URL từ locationDetail
                    contentDescription = locationDetail.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_error)
                )
                // Rating (giữ nguyên cấu trúc, lấy data từ locationDetail)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(Color(0xFFF1E8D9).copy(alpha = 0.9f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Rating",
                            tint = Color.Unspecified, // Hoặc màu khác
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            // Lấy rating từ locationDetail
                            text = String.format("%.1f", locationDetail.averageRating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // 4. Spacer trước nội dung blocks
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Hiển thị Content Blocks
        locationDetail.contentBlocks?.sortedBy { it.orderIndex }?.forEach { block ->
            item {
                when (block.blockType.uppercase()) {
                    "TEXT" -> {
                        // Hiển thị nội dung text. Lưu ý: có thể chứa HTML.
                        // Hiện tại chỉ hiển thị raw text.
                        Text(
                            text = block.contentValue, // Cần xử lý HTML nếu muốn hiển thị đúng định dạng
                            style = MaterialTheme.typography.bodyLarge, // Dùng bodyLarge cho nội dung chính
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    "IMAGE" -> {
                        // Hiển thị ảnh và chú thích (nếu có)
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            AsyncImage(
                                model = block.contentValue, // URL ảnh từ contentValue
                                contentDescription = block.caption ?: "Hình ảnh ${locationDetail.title}",
                                contentScale = ContentScale.Fit, // Fit để xem toàn bộ ảnh block
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Gray.copy(alpha = 0.1f)),
                                placeholder = painterResource(id = R.drawable.ic_placeholder),
                                error = painterResource(id = R.drawable.ic_error)
                            )
                            // Hiển thị caption nếu có
                            block.caption?.let { caption ->
                                Text(
                                    text = caption,
                                    style = MaterialTheme.typography.bodySmall, // Style nhỏ hơn cho caption
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        // Kiểu block không xác định
                    }
                }
            }
        }


        // 6. Spacer trước Comment (Tạm thời comment out phần Comment)
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }


        // --- THÊM MỚI: Phần Đánh giá ---
        item {
            val viewModel: HomeViewModel = viewModel()
            val reviewState by viewModel.reviewState.collectAsState()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ReviewSection(
                    reviewState = reviewState,
                    currentUserImageUrl = null,
                    onLoadMore = { viewModel.loadMoreReviews() },
                    onAddReview = { rating, comment ->
                        viewModel.addReview(
                            targetType = "LOCATION",
                            targetId = locationDetail.id,
                            rating = rating,
                            comment = comment
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                Text("Đánh giá chỉ hỗ trợ trên Android 8.0 trở lên", modifier = Modifier.padding(16.dp))
            }
        }

    }
}