package com.example.vivu_application.view.home

import android.os.Build
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
import androidx.compose.ui.res.painterResource // Có thể vẫn cần cho icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // Import AsyncImage
import com.example.vivu_application.data.model.TourDetail
// Import các composable phụ trợ nếu có (ví dụ: CombinedExpandableContent)
import com.example.vivu_application.ui.components.CombinedExpandableContent

import com.example.vivu_application.viewmodel.HomeViewModel
import com.example.vivu_application.ui.components.ReviewSection
import com.example.vivu_application.R
// Import InfoRow (nếu nó ở file khác)
// import com.example.loginpage.view.home.InfoRow


// Đổi tên và sửa tham số
@Composable
fun TourDetailScreenContent(tourDetail: TourDetail) {

    // Sử dụng LazyColumn làm thành phần gốc cho nội dung cuộn
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)// Padding cho nội dung
    ) {
        // 1. Spacer giữ khoảng cách từ top (có thể điều chỉnh)
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }

        // 2. Tiêu đề bài viết
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
                modifier = Modifier
                    .fillParentMaxWidth() // Chiếm hết chiều rộng item
                    .padding(top = 10.dp) // Giữ nguyên padding top 80dp bên trong này
            ) {
                Text(
                    text = "WELCOME TO", // Có thể đổi tiêu đề chung
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                // Lấy title từ tourDetail
                Text(
                    text = tourDetail.title,
                    style = MaterialTheme.typography.headlineSmall.copy( // Dùng style khác cho tên tour
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    //modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // 3. Ảnh chính và Rating
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth() // Chiếm hết chiều rộng item
                    .wrapContentSize(Alignment.Center) // Đặt Box vào giữa
                    .width(400.dp) // Giữ nguyên width
                    .height(200.dp) // Giữ nguyên height
                    .clip(RoundedCornerShape(40.dp))
            ) {
                // Sử dụng AsyncImage để tải ảnh từ URL
                AsyncImage(
                    model = tourDetail.mainImageUrl, // Lấy URL từ tourDetail
                    contentDescription = tourDetail.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    placeholder = painterResource(id = R.drawable.ic_placeholder), // Ảnh chờ
                    error = painterResource(id = R.drawable.ic_error) // Ảnh lỗi
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
                            contentDescription = "Rating",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            // Lấy rating từ tourDetail
                            text = String.format("%.1f", tourDetail.averageRating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // 4. Spacer
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 5. Thông tin chi tiết (InfoRows)
        item {
            Text(
                text = "Detailed Information:",
                style = MaterialTheme.typography.titleMedium, // Dùng style rõ ràng hơn
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(modifier = Modifier.fillParentMaxWidth()) {
                // Lấy dữ liệu từ tourDetail, xử lý null bằng ?: "N/A"
                tourDetail.locationName?.let { InfoRow(icon = R.drawable.ic_location, text = it) }
                tourDetail.itineraryDuration?.let {
                    InfoRow(
                        icon = R.drawable.ic_time,
                        text = "Itinerary: $it"
                    )
                }
                tourDetail.departureDate?.let {
                    InfoRow(
                        icon = R.drawable.ic_calendar,
                        text = "Departure: $it"
                    )
                }
                InfoRow(
                    icon = R.drawable.ic_seat,
                    text = "Available Seats: ${tourDetail.availableSlots}"
                )
                tourDetail.contactPhone?.let {
                    InfoRow(
                        icon = R.drawable.ic_contact,
                        text = "Contact: $it"
                    )
                } // Cần icon ic_contact
                tourDetail.tourCode?.let {
                    InfoRow(
                        text = "Tour Code: $it",
                        icon = R.drawable.codetour
                    )
                } // Thụt lề cho mã tour
                Spacer(modifier = Modifier.height(8.dp)) // Spacer cuối cùng
            }
        }

        // 6. Spacer trước nội dung
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 7. Nội dung mô tả (Expandable)
        item {
            Text(
                text = "Description:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Sử dụng CombinedExpandableContent nếu bạn đã có nó
            // Hoặc hiển thị Text đơn giản nếu chưa có hoặc nội dung không cần mở rộng
            // Lưu ý: tourDetail.content có thể là HTML, Text thường sẽ hiển thị raw HTML.
            if (tourDetail.content != null) {
//                Text(
//                    text = tourDetail.content ?: "Không có mô tả.",
//                    style = MaterialTheme.typography.bodyMedium
//                )
                // // --- HOẶC DÙNG CombinedExpandableContent ---
                CombinedExpandableContent(
                    initialContent = tourDetail.content ?: "Không có mô tả.",
                    detailContents = emptyList(), // Chỉ dùng initialContent cho mô tả tour này
                    maxLinesCollapsed = 5
                )
            } else {
                Text("No description available..", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // 8. Spacer trước ảnh lịch trình
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 9. Ảnh lịch trình (chỉ hiển thị nếu có URL)
        tourDetail.scheduleImageUrl?.let { imageUrl ->
            item {
                Text(
                    text = "Tentative Schedule :",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AsyncImage(
                    model = imageUrl, // Lấy URL từ tourDetail
                    contentDescription = "Lịch trình Tour ${tourDetail.title}",
                    contentScale = ContentScale.Crop, // để thấy toàn bộ ảnh lịch trình
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .wrapContentSize(Alignment.Center) // Đặt vào giữa
                        //.padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)), // Placeholder
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_error)
                )
            }
        }

        // 10. Spacer trước Comment (Tạm thời comment out phần Comment)
        item {
            Spacer(modifier = Modifier.height(5.dp))
        }

        // 11. Comment Section (Tạm thời loại bỏ vì phụ thuộc PostController)
        // --- THÊM MỚI: Phần Đánh giá ---
        item { // item này chứa toàn bộ khu vực đánh giá
            val viewModel: HomeViewModel = viewModel()
            val reviewState by viewModel.reviewState.collectAsState()
            // val currentUserImageUrl by viewModel.currentUserImageUrl.collectAsState() // Tạm thời không cần dòng này

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ReviewSection(
                    reviewState = reviewState,
                    currentUserImageUrl = null,
                    onLoadMore = { viewModel.loadMoreReviews() },
                    onAddReview = { rating, comment ->
                        viewModel.addReview(
                            targetType = "TOUR",
                            targetId = tourDetail.id,
                            rating = rating,
                            comment = comment
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                // Tuỳ chọn: Hiển thị thông báo thay thế hoặc không hiển thị gì
                Text("Reviews only available on Android 8.0 and above", modifier = Modifier.padding(16.dp))
            }
        }

    }
}