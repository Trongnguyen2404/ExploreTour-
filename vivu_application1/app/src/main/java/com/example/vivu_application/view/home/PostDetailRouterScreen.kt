package com.example.vivu_application.view.home


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vivu_application.navigation.BottomNavigationBar
import com.example.vivu_application.ui.components.CloudAnimationScreen
import com.example.vivu_application.viewmodel.DetailData // Import state data mới
import com.example.vivu_application.viewmodel.HomeViewModel // Import ViewModel

// Đổi tên thành RouterScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailRouterScreen(
    navController: NavController,
    itemId: Int,
    itemType: String, // "tour" hoặc "location"
    homeViewModel: HomeViewModel = viewModel() // Lấy HomeViewModel
) {
    val detailState by homeViewModel.detailUiState.collectAsState()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // Trigger fetch dữ liệu khi itemId hoặc itemType thay đổi (và khác -1)
    LaunchedEffect(itemId, itemType) {
        if (itemId != -1) {
            Log.d("PostDetailRouterScreen", "LaunchedEffect: Fetching detail for ID $itemId, Type: $itemType")
            when (itemType.lowercase()) {
                "tour" -> homeViewModel.fetchTourDetail(itemId)
                "location" -> homeViewModel.fetchLocationDetail(itemId)
                else -> Log.e("PostDetailRouterScreen", "Unknown item type: $itemType")
            }
        }
    }

    // Clear state khi composable bị hủy (rời khỏi màn hình)
    DisposableEffect(Unit) {
        onDispose {
            Log.d("PostDetailRouterScreen", "DisposableEffect: Clearing detail state.")
            homeViewModel.clearDetailState()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        containerColor = Color.Transparent,
        bottomBar = {
            if (!imeVisible) {
                BottomNavigationBar(navController)
            }
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Không áp dụng innerPadding ở đây nếu muốn CloudAnimation vẽ dưới BottomBar
                .padding(innerPadding)
            // Áp dụng padding an toàn cho content chính bên dưới
        ) {
            // Cloud Animation (vẽ dưới cùng)
            CloudAnimationScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = -50.dp.toPx() // tương đương offset
                    }
                    .zIndex(2f),
            )

            // Hiển thị nội dung dựa trên detailState.detailData
            when (val data = detailState.detailData) {
                is DetailData.Loading -> {
                    // Hiển thị loading indicator
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailData.TourSuccess -> {
                    // Gọi Composable hiển thị nội dung Tour
                    TourDetailScreenContent(tourDetail = data.tourDetail)
                }
                is DetailData.LocationSuccess -> {
                    // Gọi Composable hiển thị nội dung Location
                    LocationDetailScreenContent(locationDetail = data.locationDetail)
                }
                is DetailData.Error -> {
                    // Hiển thị thông báo lỗi
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Lỗi tải dữ liệu: ${data.message}",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                // Thử lại fetch
                                if (itemId != -1) {
                                    when (itemType.lowercase()) {
                                        "tour" -> homeViewModel.fetchTourDetail(itemId)
                                        "location" -> homeViewModel.fetchLocationDetail(itemId)
                                    }
                                }
                            }) {
                                Text("Thử lại")
                            }
                        }
                    }
                }
                is DetailData.Idle -> {
                    // Trạng thái ban đầu hoặc đã clear, có thể hiển thị loading nhẹ hoặc không gì cả
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Text("Đang chuẩn bị...") // Hoặc để trống
                        CircularProgressIndicator(strokeWidth = 2.dp) // Loading nhẹ
                    }
                }
            }
        }
    }
}
