package com.example.vivu_application.view.favorites

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush // THÊM NẾU DÙNG BRUSH NHƯ POSTITEM
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vivu_application.R
import com.example.vivu_application.data.model.ApiFavoriteItem
import com.example.vivu_application.data.model.Location
import com.example.vivu_application.data.model.Tour
import com.example.vivu_application.navigation.BottomNavigationBar
import com.example.vivu_application.viewmodel.FavoriteViewModel
import com.example.vivu_application.viewmodel.FavoritesUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

// Composable FavoritesScreen giữ nguyên như đã cung cấp ở bước trước
@Composable
fun FavoritesScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val uiState by favoriteViewModel.uiState.collectAsState()
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val lazyListState = rememberLazyListState()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars, // Không chịu ảnh hưởng bàn phím

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1E8D9))
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ){
                if (!imeVisible) {
                    BottomNavigationBar(navController,)
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_favorites),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp + statusBarHeight)
                    .graphicsLayer {
                        translationY = -statusBarHeight.toPx()
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_vivu),
                    contentDescription = "Vivu Logo",
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.TopStart)
                        .padding(bottom = 35.dp)
                )

                Text(
                    text = "Favorites list",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 40.dp, bottom = 80.dp)
                )
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
            ) {
                if (uiState.isLoading && uiState.favoriteItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.error != null && uiState.favoriteItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else if (uiState.favoriteItems.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "There are no favorite posts.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        // Giữ padding của Card trong FavoriteListItem thay vì ở đây để nhất quán với PostItem
                        // contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        // verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favoriteItems, key = { item -> "${item.favoriteType}_${item.itemId}" }) { favoriteItem ->
                            FavoriteListItem( // SỬ DỤNG FavoriteListItem ĐÃ ĐƯỢC SỬA ĐỔI
                                apiFavoriteItem = favoriteItem,
                                onRemoveClick = {
                                    favoriteViewModel.removeFavoriteItem(favoriteItem.itemId, favoriteItem.favoriteType)
                                },
                                onItemClick = {
                                    val route = when (favoriteItem.favoriteType) {
                                        "TOUR" -> "tourDetail/${favoriteItem.itemId}"
                                        "LOCATION" -> "locationDetail/${favoriteItem.itemId}"
                                        else -> null
                                    }
                                    route?.let { navController.navigate(it) }
                                }
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else if (uiState.error != null && uiState.favoriteItems.isNotEmpty() && !uiState.isLoadingMore) {
                            item {
                                Text(
                                    "Error loading more: ${uiState.error}",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(lazyListState, uiState.isLoadingMore, uiState.isLastPage) {
        snapshotFlow { lazyListState.layoutInfo }
            .map { layoutInfo ->
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 5 && !uiState.isLoadingMore && !uiState.isLastPage
            }
            .distinctUntilChanged()
            .filter { shouldLoadMore -> shouldLoadMore }
            .collect {
                Log.d("FavoritesScreen", "Threshold reached for favorites, triggering load next page.")
                favoriteViewModel.loadFavoriteItems()
            }
    }
}


// --- SỬA ĐỔI FavoriteListItem Ở ĐÂY ---
@Composable
fun FavoriteListItem(
    apiFavoriteItem: ApiFavoriteItem,
    onRemoveClick: () -> Unit,
    onItemClick: () -> Unit
) {
    // Lấy thông tin từ ApiFavoriteItem, tương tự như cách PostItem lấy từ DisplayItem
    val imageUrl: String?
    val ratingValue: Double
    val title: String
    val itemTypeSpecificDetails: @Composable ColumnScope.() -> Unit // Lambda để hiển thị chi tiết theo loại

    var isCardClicked by remember { mutableStateOf(false) } // Cho hiệu ứng click nếu muốn
    val cardBackgroundBrush = if (isCardClicked) {
        Brush.horizontalGradient(colors = listOf(Color(0xFFEFB0C9), Color(0xFFB9D6F3)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFE4DEE1), Color(0xFFE4DEE1))) // Màu nền giống PostItem
    }

    when (apiFavoriteItem.favoriteType) {
        "TOUR" -> {
            val tour = apiFavoriteItem.tour
            imageUrl = tour?.mainImageUrl
            ratingValue = tour?.averageRating ?: 0.0
            title = tour?.title ?: "Tour không tên"
            itemTypeSpecificDetails = {
                InfoRow(icon = R.drawable.ic_location, text = tour?.locationName ?: title)
                InfoRow(icon = R.drawable.ic_time, text = "Itinerary: ${tour?.itineraryDuration ?: "N/A"}")
                InfoRow(icon = R.drawable.ic_calendar, text = "Departure: ${tour?.departureDate ?: "N/A"}")
                InfoRow(icon = R.drawable.ic_seat, text = "Available Seats: ${tour?.availableSlots ?: "N/A"}")
            }
        }
        "LOCATION" -> {
            val location = apiFavoriteItem.location
            imageUrl = location?.headerImageUrl
            ratingValue = location?.averageRating ?: 0.0
            title = location?.title ?: "Địa điểm không tên"
            itemTypeSpecificDetails = {
                InfoRow(icon = R.drawable.ic_location, text = title)
                // Thêm các InfoRow khác cho Location nếu có và cần thiết
            }
        }
        else -> { // Trường hợp không xác định
            imageUrl = null
            ratingValue = 0.0
            title = "Unspecified item"
            itemTypeSpecificDetails = { Text("No details") }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp) // Giống PostItem
            .padding(horizontal = 16.dp, vertical = 8.dp) // Giống PostItem
            .clickable {
                isCardClicked = !isCardClicked // Nếu muốn hiệu ứng click đổi màu
                onItemClick()
            }
            .background(brush = cardBackgroundBrush, shape = RoundedCornerShape(40.dp)), // Giống PostItem
        shape = RoundedCornerShape(40.dp), // Giống PostItem
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Giống PostItem
    ) {
        Row(
            modifier = Modifier.padding(10.dp), // Giống PostItem
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box Ảnh và Rating
            Box(
                modifier = Modifier
                    .width(155.dp) // Giống PostItem
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_error)
                )

                // Hiển thị Rating (nếu có ratingValue > 0)
                if (ratingValue > 0) {
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
                                modifier = Modifier.size(14.dp) // Điều chỉnh size nếu cần
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f", ratingValue),
                                fontSize = 14.sp, // Điều chỉnh size nếu cần
                                fontWeight = FontWeight.Bold, // Điều chỉnh font weight nếu cần
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(5.dp)) // Giống PostItem

            // Column thông tin chi tiết
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center, // Giống PostItem
                horizontalAlignment = Alignment.Start
            ) {
                // Tiêu đề chính của item
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold, // Có thể muốn tiêu đề đậm hơn
                    fontSize = 16.sp, // Điều chỉnh nếu cần
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                // Gọi lambda để hiển thị chi tiết theo loại (Tour/Location)
                itemTypeSpecificDetails()
            }

            // Nút xóa yêu thích
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.align(Alignment.Top) // Đặt ở góc trên bên phải của Row
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Xóa khỏi yêu thích",
                    tint = MaterialTheme.colorScheme.error // Màu đỏ cho nút xóa
                )
            }
        }
    }
}
// Composable InfoRow (NẾU CHƯA CÓ TRONG FILE NÀY, BẠN CẦN COPY NÓ TỪ PostItem.kt VÀO ĐÂY)
// Hoặc import nếu nó nằm ở file khác và có thể truy cập được.
// Giả sử bạn copy nó vào đây:

@Composable
fun InfoRow(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp) // Thêm chút padding cho mỗi dòng
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            // tint = Color.DarkGray // Có thể bỏ tint để giữ màu gốc của icon
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 13.sp, // Giảm nhẹ size cho các dòng chi tiết
            // fontWeight = FontWeight.Medium, // Giữ nguyên
            color = Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
