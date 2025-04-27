package com.example.vivu_application.ui.components // Hoặc package phù hợp

import android.os.Build
// import android.text.format.DateUtils.formatDateTime // Không còn dùng trong ReviewItem này
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border // Thêm import
// import androidx.compose.foundation.background // Không dùng background nữa
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
// import androidx.compose.foundation.shape.CircleShape // Không dùng CircleShape nữa
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.draw.clip // Không dùng clip nữa
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.res.painterResource // Không cần nếu dùng Icon/AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.material3.Text
import com.example.vivu_application.data.model.Review
import com.example.vivu_application.model.ReviewUser
import com.example.loginpage.ui.components.StarRating
import java.time.OffsetDateTime // Vẫn cần cho formatDateTime
import java.time.ZoneId       // Vẫn cần cho formatDateTime
import java.time.format.DateTimeFormatter // Vẫn cần cho formatDateTime
import java.time.format.FormatStyle       // Vẫn cần cho formatDateTime
// import java.time.LocalDateTime // Không dùng trực tiếp nữa
import com.example.vivu_application.R
// --- ReviewItem đã được sửa đổi ---
@Composable
fun ReviewItem(review: Review, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 6.dp)
            .width(450.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(40.dp) // Bo tròn viền ngoài
            )
            .padding(8.dp) // Padding giữa viền và nội dung
    ) {
        Row(
            verticalAlignment = Alignment.Top // Căn lề trên giống CommentItem
        ) {
            // Avatar bên trái (Dùng AsyncImage thay vì Icon)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(review.user?.profilePictureUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_placeholder_person)
                    .error(R.drawable.ic_placeholder_person)
                    .build(),
                contentDescription = "Ảnh đại diện của ${review.user?.fullName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .offset(y = 5.dp)
                    .clip(CircleShape) // Thêm dòng này để bo tròn
            )

            Spacer(modifier = Modifier.width(2.dp)) // Khoảng cách giống CommentItem

            // Nội dung đánh giá bên phải trong nền sáng
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.width(270.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {

                    // Hàng đầu: tên + đánh giá sao sát nhau
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = review.user?.fullName ?: "Người dùng ẩn danh",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) // Style giống CommentItem
                        )

                        Spacer(modifier = Modifier.width(4.dp)) // Khoảng cách giống CommentItem

                        // Sử dụng StarRating composable (đảm bảo nó tồn tại trong project)
                        StarRating( // Gọi StarRating
                            rating = review.rating,
                            editable = false,
                            iconSize = 12.dp, // Kích thước nhỏ giống CommentItem
                            spacing = 1.dp    // Khoảng cách nhỏ giống CommentItem
                        )
                        // Không hiển thị ngày tháng ở đây nữa
                    }

                    Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giống CommentItem

                    // Nội dung bình luận/đánh giá

                        ExpandableText(text = review.comment ?: "")
                }
            }
        }
    }
}

// --- Hàm tiện ích format ngày tháng (Giữ lại nếu cần dùng ở nơi khác) ---
// Cần API 26+
@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(dateTimeString: String): String {
    return try {
        val odt = OffsetDateTime.parse(dateTimeString)
        // Chuyển sang múi giờ địa phương (tùy chọn)
        val localDateTime = odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        // Format theo kiểu ngắn gọn
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        localDateTime.format(formatter)
    } catch (e: Exception) {
        // Trả về chuỗi gốc nếu không parse được
        dateTimeString.split("T").firstOrNull() ?: dateTimeString // Lấy phần ngày nếu lỗi
    }
}
// --- Preview cho ReviewItem (Đã cập nhật để phản ánh style mới) ---
@RequiresApi(Build.VERSION_CODES.O) // Vẫn cần vì Preview dùng sample data có ngày tháng
@Preview(showBackground = true, name = "Review Item New Style")
@Composable
fun ReviewItemPreview() {
    val sampleReview = Review(
        id = 1,
        user = ReviewUser(id = 1, fullName = "Nguyễn Văn A", profilePictureUrl = null), // Thử với null
        targetType = "TOUR",
        targetId = 1,
        rating = 4,
        comment = "Chuyến đi rất tuyệt vời, hướng dẫn viên nhiệt tình, cảnh đẹp. Chỉ có đồ ăn hơi ít lựa chọn.",
        createdAt = "2024-01-15T10:30:00Z", // Dữ liệu vẫn tồn tại dù không hiển thị trong item này
        updatedAt = "2024-01-15T10:30:00Z"
    )
    MaterialTheme {
        // Box để dễ xem padding ngoài của ReviewItem
        Box(modifier = Modifier.padding(16.dp)) {
            ReviewItem(review = sampleReview)
        }
    }
}