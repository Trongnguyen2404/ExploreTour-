package com.example.vivu_application.view.home

import android.util.Log
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
    homeViewModel: HomeViewModel, // Giữ nguyên ViewModel
    modifier: Modifier = Modifier
) {
    // --- KHÔNG THAY ĐỔI LOGIC STATE ---
    val listUiState by homeViewModel.listUiState.collectAsState()
    val items = listUiState.items
    val isLoading = listUiState.isLoading
    val isLastPage = listUiState.isLastPage
    val error = listUiState.error
    // --- KẾT THÚC KHÔNG THAY ĐỔI ---

    val lazyListState = rememberLazyListState() // Giữ nguyên state của LazyColumn

    Log.d("PostListScreen", "Displaying ${items.size} items. isLoading: $isLoading, isLastPage: $isLastPage")

    LazyColumn(
        modifier = modifier,
        state = lazyListState // Giữ nguyên gán state
    ) {
        // --- KHÔNG THAY ĐỔI LOGIC HIỂN THỊ ITEMS ---
        items(
            items = items,
            key = { item -> item.id } // Giữ nguyên key
        ) { item ->
            PostItem( // Truyền DisplayItem, NavController, ViewModel, onFavoriteClick
                displayItem = item,
                navController = navController,
                viewModel = homeViewModel, // Truyền homeViewModel
                onFavoriteClick = {
                    if (item is DisplayItem.TourItem) {
                        Log.d("PostListScreen", "Favorite clicked for item ID: ${item.id}")
                        homeViewModel.toggleFavorite(item.id) // Giữ nguyên logic favorite
                    } else {
                        Log.d("PostListScreen", "Favorite clicked for non-tour item ID: ${item.id} - Ignoring")
                    }
                }
            )
        }
        // --- KẾT THÚC KHÔNG THAY ĐỔI ---

        // --- KHÔNG THAY ĐỔI LOGIC LOADING/ERROR CUỐI DANH SÁCH ---
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
                    "Lỗi tải thêm: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
        // --- KẾT THÚC KHÔNG THAY ĐỔI ---
    }

    // --- KHÔNG THAY ĐỔI LOGIC LOAD MORE ---
    LaunchedEffect(lazyListState, isLoading, isLastPage) {
        snapshotFlow { lazyListState.layoutInfo }
            .map { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 6 && !isLoading && !isLastPage
            }
            .distinctUntilChanged()
            .filter { shouldLoadMore -> shouldLoadMore }
            .collect {
                Log.d("PostListScreen", "Threshold reached, triggering load next page.")
                homeViewModel.loadNextPage() // Giữ nguyên gọi load next page
            }
    }
    // --- KẾT THÚC KHÔNG THAY ĐỔI ---
}


@Composable
fun PostItem(
    displayItem: DisplayItem, // Giữ nguyên DisplayItem
    navController: NavController,
    viewModel: HomeViewModel, // Giữ nguyên ViewModel
    onFavoriteClick: () -> Unit
) {
    // --- KHÔNG THAY ĐỔI LOGIC FAVORITE STATE ---
    val favoriteItemIds by viewModel.favoriteItemIds.collectAsState()
    val isFavorited = favoriteItemIds.contains(displayItem.id)
    // --- KẾT THÚC KHÔNG THAY ĐỔI ---

    var isClicked by remember { mutableStateOf(false) } // Giữ nguyên state click
    // Giữ nguyên logic background brush
    val cardBackgroundBrush = if (isClicked) {
        Brush.horizontalGradient(colors = listOf(Color(0xFFEFB0C9), Color(0xFFB9D6F3)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFE4DEE1), Color(0xFFE4DEE1)))
    }

    Card(
        modifier = Modifier
            // --- CẬP NHẬT Ở ĐÂY ---
            .fillMaxWidth() // Bỏ width cố định, giữ fillMaxWidth
            .height(170.dp) // Giữ nguyên height
            .padding(horizontal = 16.dp, vertical = 8.dp) // Giữ nguyên padding card
            .clickable { // Giữ nguyên logic clickable và điều hướng
                isClicked = !isClicked
                Log.d("PostItem", "Card clicked, navigating for item ID: ${displayItem.id}, type: ${displayItem::class.simpleName}")
                val route = when(displayItem) {
                    is DisplayItem.TourItem -> "tourDetail/${displayItem.id}" // Dùng id
                    is DisplayItem.LocationItem -> "locationDetail/${displayItem.id}" // Dùng id
                }
                navController.navigate(route) // Giữ nguyên điều hướng
            }
            .background(brush = cardBackgroundBrush, shape = RoundedCornerShape(40.dp)), // Giữ nguyên background
        shape = RoundedCornerShape(40.dp), // Giữ nguyên shape
        // elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Giữ nguyên elevation
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Giữ nguyên colors
    ) {
        Row(
            modifier = Modifier.padding(10.dp), // Giữ nguyên padding Row
            verticalAlignment = Alignment.CenterVertically // Giữ nguyên alignment
        ) {
            // Box Ảnh và Rating (Giữ nguyên cấu trúc, dùng AsyncImage và dữ liệu từ displayItem)
            Box(
                modifier = Modifier
                    .width(155.dp) // Giữ nguyên width
                    .fillMaxHeight() // Giữ nguyên fillMaxHeight
                    .clip(RoundedCornerShape(16.dp)) // Giữ nguyên clip
                // .background(Color.Gray.copy(alpha = 0.1f)) // Giữ nguyên background dự phòng
            ) {
                // --- KHÔNG THAY ĐỔI LOGIC LẤY DỮ LIỆU ---
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
                // --- KẾT THÚC KHÔNG THAY ĐỔI ---

                // Giữ nguyên AsyncImage
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDesc,
                    contentScale = ContentScale.Crop, // Giữ nguyên contentScale
                    modifier = Modifier.matchParentSize(), // Giữ nguyên modifier
                    placeholder = painterResource(id = R.drawable.ic_placeholder), // Giữ nguyên placeholder
                    error = painterResource(id = R.drawable.ic_error) // Giữ nguyên error
                )

                // Hiển thị Rating (Giữ nguyên UI và logic)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart) // Giữ nguyên alignment
                        .padding(6.dp) // Giữ nguyên padding
                        .width(70.dp) // Giữ nguyên width
                        .height(20.dp) // Giữ nguyên height
                        .background(Color(0xFFF1E8D9), RoundedCornerShape(50.dp)), // Giữ nguyên background
                    contentAlignment = Alignment.Center // Giữ nguyên alignment
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
                            modifier = Modifier.size(20.dp) // Cập nhật size từ file mới là 20, nhưng file cũ là 14, giữ 14
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", ratingValue), // Định dạng rating
                            fontSize = 16.sp, // Cập nhật size từ file mới là 16, giữ 14 cho nhất quán
                            fontWeight = FontWeight.Medium, // Cập nhật weight từ file mới là Medium, giữ Bold
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(5.dp)) // Giữ spacer width 12dp (file mới là 5dp)

            // Column thông tin chi tiết (Giữ nguyên logic hiển thị InfoRow theo type)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), // Giữ nguyên fillMaxHeight
                // --- CẬP NHẬT Ở ĐÂY ---
                verticalArrangement = Arrangement.Center, // Cập nhật verticalArrangement từ SpaceEvenly -> Center
                horizontalAlignment = Alignment.Start // Giữ nguyên horizontalAlignment
            ) {
                // --- KHÔNG THAY ĐỔI LOGIC HIỂN THỊ InfoRow ---
                when (displayItem) {
                    is DisplayItem.TourItem -> {
                        val tour = displayItem.tour
                        InfoRow(icon = R.drawable.ic_location, text = tour.locationName ?: tour.title)
                        InfoRow(icon = R.drawable.ic_time, text = "Lịch trình: ${tour.itineraryDuration ?: "N/A"}")
                        InfoRow(icon = R.drawable.ic_calendar, text = "Khởi hành: ${tour.departureDate ?: "N/A"}")
                        InfoRow(icon = R.drawable.ic_seat, text = "Số chỗ còn: ${tour.availableSlots}")
                    }
                    is DisplayItem.LocationItem -> {
                        val location = displayItem.location
                        InfoRow(icon = R.drawable.ic_location, text = location.title)
                        // Bỏ Spacer nếu dùng Arrangement.Center
                        // Spacer(modifier = Modifier.weight(1f))
                    }
                }
                // --- KẾT THÚC KHÔNG THAY ĐỔI ---
            }

            // Nút yêu thích (Cập nhật padding, giữ nguyên logic)
            if (displayItem is DisplayItem.TourItem) {
                Icon(
                    painter = painterResource(
                        id = if (isFavorited) R.drawable.favorite_icon1 else R.drawable.favorite_icon
                    ),
                    contentDescription = if (isFavorited) "Hủy yêu thích" else "Thêm vào yêu thích",
                    tint = if (isFavorited) Color.Red else Color.Gray,
                    modifier = Modifier
                        // --- CẬP NHẬT Ở ĐÂY ---
                        .padding(end = 10.dp) // Cập nhật padding từ start = 4.dp -> end = 10.dp
                        .align(Alignment.Top) // Giữ nguyên alignment
                        .size(24.dp) // Giữ nguyên size
                        .clickable( // Giữ nguyên clickable và gọi onFavoriteClick
                            // interactionSource = remember { MutableInteractionSource() }, // Bỏ nếu không cần ripple
                            // indication = rememberRipple(bounded = false), // Bỏ nếu không cần ripple
                            onClick = onFavoriteClick
                        )
                )
            } else {
                // --- CẬP NHẬT Ở ĐÂY ---
                // Giữ khoảng trống để layout cân đối, cập nhật padding cho khớp icon
                Spacer(Modifier.size(24.dp).padding(end = 10.dp))
            }
        }
    }
}


// InfoRow (Đã cập nhật UI theo file mới)
@Composable
fun InfoRow(icon: Int, text: String) {
    // --- CẬP NHẬT Ở ĐÂY ---
    Row(verticalAlignment = Alignment.CenterVertically) { // Giữ modifier cũ padding vertical
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(15.dp), // Giữ nguyên size
            //tint = Color.DarkGray // Thêm tint từ file cũ
        )
        Spacer(modifier = Modifier.width(4.dp)) // Cập nhật width spacer từ 5dp -> 4dp
        Text(
            text = text,
            fontSize = 14.sp, // Cập nhật fontSize từ 14sp -> 13sp
            fontWeight = FontWeight.Medium, // Giữ nguyên fontWeight
            color = Color.DarkGray, // Giữ nguyên color
            // maxLines = 1 // Thêm maxLines từ file cũ
        )
    }
}