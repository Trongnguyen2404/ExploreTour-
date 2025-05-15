// File: com/example/vivu_application/view/home/PostListScreen.kt
package com.example.vivu_application.view.home

import androidx.compose.foundation.*
// import androidx.compose.foundation.interaction.MutableInteractionSource // Không dùng nếu bỏ ripple
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Import đúng items
import androidx.compose.foundation.lazy.rememberLazyListState // Import để theo dõi state LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.ripple.rememberRipple // Bỏ nếu không dùng ripple trong clickable
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import các hàm của compose runtime
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow // THÊM IMPORT NÀY NẾU InfoRow CẦN
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // Giữ nguyên AsyncImage
import com.example.vivu_application.model.DisplayItem // Giữ nguyên DisplayItem
import com.example.vivu_application.viewmodel.HomeViewModel// Giữ nguyên ViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import com.example.vivu_application.R

@Composable
fun PostListScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val listUiState by homeViewModel.listUiState.collectAsState()
    val items = listUiState.items
    val isLoading = listUiState.isLoading
    val isLastPage = listUiState.isLastPage
    val error = listUiState.error

    val lazyListState = rememberLazyListState()


    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            PostItem(
                displayItem = item,
                navController = navController,
                viewModel = homeViewModel,
                onFavoriteClick = {
                    // --- THAY ĐỔI CHÍNH Ở ĐÂY ---
                    when (item) {
                        is DisplayItem.TourItem -> {
                            // Gọi toggleFavorite với itemType là "TOUR"
                            // Giả định HomeViewModel đã được cập nhật để có hàm này
                            homeViewModel.toggleFavorite(item.id, "TOUR")
                        }
                        is DisplayItem.LocationItem -> {
                            // Nếu bạn muốn cho phép yêu thích Location từ Home và PostItem có hiển thị nút tim cho Location:

                            homeViewModel.toggleFavorite(item.id, "LOCATION")
                            // Nếu PostItem không hiển thị nút tim cho Location, thì đoạn code trên sẽ không được gọi.
                            // Dựa trên PostItem bạn cung cấp, có vẻ nút tim chỉ hiển thị khi item là TourItem.
                            // Nếu bạn muốn Location cũng có thể được yêu thích từ đây, bạn cần sửa cả PostItem.
                        }
                        // else -> {
                        //    Log.w("PostListScreen", "Favorite clicked for unhandled item type: ${item::class.simpleName}")
                        // }
                    }
                }
            )
        }

        item {
            if (isLoading && items.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            if (error != null && items.isNotEmpty() && !isLoading) {
                Text(
                    "Error loading more: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }

    LaunchedEffect(lazyListState, isLoading, isLastPage) {
        snapshotFlow { lazyListState.layoutInfo }
            .map { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 6 && !isLoading && !isLastPage
            }
            .distinctUntilChanged()
            .filter { shouldLoadMore -> shouldLoadMore }
            .collect {

                homeViewModel.loadNextPage()
            }
    }
}


@Composable
fun PostItem(
    displayItem: DisplayItem,
    navController: NavController,
    viewModel: HomeViewModel,
    onFavoriteClick: () -> Unit
) {
    val favoriteItemIds by viewModel.favoriteItemIds.collectAsState() // Đảm bảo HomeViewModel có favoriteItemIds
    val isFavorited = favoriteItemIds.contains(displayItem.id)

    var isClicked by remember { mutableStateOf(false) }
    val cardBackgroundBrush = if (isClicked) {
        Brush.horizontalGradient(colors = listOf(Color(0xFFEFB0C9), Color(0xFFB9D6F3)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFE4DEE1), Color(0xFFE4DEE1)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                isClicked = !isClicked
                val route = when(displayItem) {
                    is DisplayItem.TourItem -> "tourDetail/${displayItem.id}"
                    is DisplayItem.LocationItem -> "locationDetail/${displayItem.id}"
                }
                navController.navigate(route)
            }
            .background(brush = cardBackgroundBrush, shape = RoundedCornerShape(40.dp)),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(155.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                val imageUrl: String?
                val ratingValue: Double
                val contentDesc: String
                when (displayItem) {
                    is DisplayItem.TourItem -> {
                        imageUrl = displayItem.tour.mainImageUrl
                        ratingValue = displayItem.tour.averageRating
                        contentDesc = displayItem.tour.title
                    }
                    is DisplayItem.LocationItem -> {
                        imageUrl = displayItem.location.headerImageUrl
                        ratingValue = displayItem.location.averageRating
                        contentDesc = displayItem.location.title
                    }
                }
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDesc,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_error)
                )

                // Hiển thị Rating (Giữ nguyên UI và logic từ file bạn cung cấp)
                // (Đã có trong file bạn cung cấp, nên không cần thêm ở đây)
                if (ratingValue > 0) { // Chỉ hiển thị nếu có rating
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
                                contentDescription = "Rating",
                                tint = Color.Unspecified, // Giữ màu gốc của icon
                                modifier = Modifier.size(14.dp) // Điều chỉnh size nếu cần, 14dp hoặc 20dp như trong file bạn cung cấp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f", ratingValue),
                                fontSize = 14.sp, // Điều chỉnh size nếu cần
                                fontWeight = FontWeight.Bold, // Medium hoặc Bold
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(5.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                when (displayItem) {
                    is DisplayItem.TourItem -> {
                        val tour = displayItem.tour
                        // Tiêu đề của TourItem
                        Text(
                            text = tour.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1, // Giữ 1 dòng cho tiêu đề để các InfoRow không bị đẩy xuống quá nhiều
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        InfoRow(icon = R.drawable.ic_location, text = tour.locationName ?: tour.title)
                        InfoRow(icon = R.drawable.ic_time, text = "Itinerary: ${tour.itineraryDuration ?: "N/A"}")
                        InfoRow(icon = R.drawable.ic_calendar, text = "Departure: ${tour.departureDate ?: "N/A"}")
                        InfoRow(icon = R.drawable.ic_seat, text = "Available Seats: ${tour.availableSlots}")
                    }
                    is DisplayItem.LocationItem -> {
                        val location = displayItem.location
                        // Tiêu đề của LocationItem
                        Text(
                            text = location.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 2, // Có thể cho 2 dòng nếu tiêu đề Location thường dài hơn
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        InfoRow(icon = R.drawable.ic_location, text = location.title)
                        // Thêm các InfoRow khác cho Location nếu cần
                    }
                }
            }

            // Nút yêu thích
            // Sửa đổi điều kiện hiển thị nút tim:
            // Nếu muốn cả Tour và Location đều có thể yêu thích từ Home và có nút tim:
            val canBeFavorited = displayItem is DisplayItem.TourItem || displayItem is DisplayItem.LocationItem
            if (canBeFavorited) {
                Icon(
                    painter = painterResource(
                        id = if (isFavorited) R.drawable.favorite_icon1 else R.drawable.favorite_icon
                    ),
                    contentDescription = if (isFavorited) "Hủy yêu thích" else "Thêm vào yêu thích",
                    tint = if (isFavorited) Color.Red else Color.Gray,
                    modifier = Modifier
                        .padding(end = 10.dp) // Điều chỉnh padding của icon
                        .align(Alignment.Top)
                        .size(24.dp)
                        .clickable(onClick = onFavoriteClick)
                )
            } else {
                // Giữ khoảng trống nếu item không thể được yêu thích từ đây
                Spacer(Modifier.size(24.dp).padding(end = 0.dp, start = 4.dp))
            }
        }
    }
}

// InfoRow (Giữ nguyên như bạn đã cung cấp, hoặc điều chỉnh style nếu muốn)
@Composable
fun InfoRow(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp) // Giảm padding vertical để vừa hơn
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 13.sp, // Font size cho chi tiết
            color = Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}