package com.example.loginpage.ui.components // Hoặc package phù hợp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder // Sử dụng StarBorder thay vì Star outlined
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vivu_application.data.model.Review // Model Review gốc
import com.example.vivu_application.model.ReviewUser
import com.example.vivu_application.ui.components.ReviewItem
import com.example.vivu_application.viewmodel.ReviewUiState // Import ReviewUiState
import com.example.vivu_application.R
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O) // Cần cho ReviewItem
@Composable
fun ReviewSection(
    reviewState: ReviewUiState,           // State hiển thị danh sách review
    currentUserImageUrl: String?,         // URL ảnh đại diện của người dùng hiện tại
    onLoadMore: () -> Unit,               // Lambda để tải thêm review
    onAddReview: (rating: Int, comment: String) -> Unit, // Lambda để gửi review mới
    modifier: Modifier = Modifier
) {
    var newCommentText by remember { mutableStateOf("") }
    var newRating by remember { mutableStateOf(0) }

    Column( modifier = Modifier.fillMaxWidth().padding(16.dp)) { // Padding tổng thể
        Box(

            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(300.dp)
                .height(1.dp)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(16.dp))
        // 1. Tiêu đề phần đánh giá (Giữ nguyên hoặc tùy chỉnh)
        Text(
            text = "COMMENT (${reviewState.reviews.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // 2. Phần nhập đánh giá mới
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rating stars để chọn
            StarRating(
                rating = newRating,
                onRatingChanged = { newRating = it },
                editable = true,
                iconSize = 32.dp // Kích thước sao lớn hơn cho việc chọn
            )

            Text(
                text = "Chạm vào ngôi sao để xếp hạng",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Chỉ hiển thị ô nhập text và nút gửi sau khi đã chọn sao
            if (newRating > 0) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    // Avatar người dùng hiện tại
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentUserImageUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.ic_placeholder_person) // Placeholder mặc định
                            .error(R.drawable.ic_placeholder_person)
                            .build(),
                        contentDescription = "Ảnh đại diện của bạn",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp) // Kích thước nhỏ hơn cho input
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // TextField + Send icon trong Box bo tròn
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .width(400.dp)
                            .heightIn(min = 50.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            //modifier = Modifier.padding(end = 4.dp) // Padding để icon không sát viền phải
                        ) {
                            TextField(
                                value = newCommentText,
                                onValueChange = { newCommentText = it },
                                placeholder = { Text("Enter the comment...", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors( // Bỏ nền và đường kẻ
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                maxLines = 3,
                                singleLine = false
                            )

                            // Nút gửi chỉ bật khi có text
                            IconButton(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        onAddReview(newRating, newCommentText)
                                        // Reset input sau khi gửi
                                        newCommentText = ""
                                        newRating = 0
                                    }
                                },
                                enabled = newCommentText.isNotBlank(),
                                modifier = Modifier.size(40.dp) // Kích thước IconButton
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_send),
                                    contentDescription = "Gửi bình luận",
                                    modifier = Modifier.size(30.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        } // Kết thúc phần nhập đánh giá mới

        // 3. Divider (tùy chọn) ngăn cách phần nhập và danh sách
        if (reviewState.reviews.isNotEmpty()) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // 4. Danh sách các review đã có (Sử dụng ReviewItem gốc)
        if (reviewState.reviews.isNotEmpty()) {
            reviewState.reviews.forEach { review ->
                // Sử dụng ReviewItem gốc của bạn
                ReviewItem(review = review, modifier = Modifier.padding(bottom = 6.dp))
                // Không cần Divider ở đây nữa nếu dùng padding cho ReviewItem
            }
        } else if (!reviewState.isLoading && reviewState.error == null && newRating == 0) {
            // Trường hợp không có review nào VÀ người dùng chưa bắt đầu đánh giá
            Text(
                text = "Chưa có đánh giá nào. Hãy là người đầu tiên!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Hiển thị Loading hoặc Nút Load More hoặc Lỗi (Giữ nguyên logic cũ)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                reviewState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
                reviewState.error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Lỗi tải đánh giá: ${reviewState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(4.dp))
                        // Nút thử lại có thể gọi lại hàm fetch hoặc loadmore tùy logic
                        Button(onClick = onLoadMore) { Text("Thử lại") }
                    }
                }
                !reviewState.isLastPage && reviewState.reviews.isNotEmpty() -> {
                    OutlinedButton(onClick = onLoadMore) {
                        Text("Xem thêm đánh giá")
                    }
                }
                reviewState.isLastPage && reviewState.reviews.isNotEmpty() -> {
                    Text("Đã xem hết đánh giá", style = MaterialTheme.typography.bodySmall)
                }
                // Không cần hiển thị gì thêm nếu list rỗng và đang không load/error
            }
        }
    } // Kết thúc Column tổng thể
}
// --- Composable StarRating (Cần có trong project của bạn) ---
@Composable
fun StarRating(
    rating: Int,
    editable: Boolean = true,
    onRatingChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp, // Kích thước mặc định nhỏ hơn
    spacing: Dp = 1.dp   // Khoảng cách nhỏ
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder, // Dùng StarBorder
                contentDescription = "Star $i",
                tint = if (i <= rating) Color(0xFFFFA000) else Color.Gray, // Màu vàng cam
                modifier = Modifier
                    .size(iconSize)
                    .then( // Chỉ thêm clickable nếu editable=true
                        if (editable) Modifier.clickable { onRatingChanged(i) } else Modifier
                    )
            )
            if (i < 5) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}

// --- Preview (Cần cập nhật để truyền đủ tham số) ---
// --- Preview (Đã cập nhật để truyền đủ tham số) ---

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "Review Section Preview") // Thêm name cho dễ phân biệt
@Composable
fun ReviewSectionPreview() {
    val sampleReviews = List(3) { index ->
        // Review và ReviewUser là các data class bạn đã định nghĩa
        Review(
            id = index + 1,
            // Giả sử bạn có data class ReviewUser
            user = ReviewUser(id = index + 1, fullName = "Người dùng ${index + 1}", profilePictureUrl = null),
            targetType = "TOUR",
            targetId = 1,
            rating = (3..5).random(),
            comment = "Đây là bình luận mẫu thứ ${index + 1}. ".repeat((1..3).random()),
            createdAt = "2024-01-1${5+index}T10:30:00Z",
            updatedAt = "2024-01-1${5+index}T10:30:00Z"
        )
    }
    val previewState = ReviewUiState(
        reviews = sampleReviews,
        isLoading = false,
        error = null, // Thử đổi thành "Some Error" để xem giao diện lỗi
        isLastPage = false // Thử đổi thành true để xem trạng thái cuối trang
    )

    MaterialTheme { // Đảm bảo có MaterialTheme bao ngoài
        ReviewSection(
            reviewState = previewState,
            currentUserImageUrl = null, // <-- Tham số mới: Cung cấp giá trị (null hoặc URL ảnh)
            onLoadMore = { /* No action in preview */ },
            onAddReview = { rating, comment -> /* No action in preview */ } // <-- Tham số mới: Cung cấp lambda rỗng
        )
    }
}