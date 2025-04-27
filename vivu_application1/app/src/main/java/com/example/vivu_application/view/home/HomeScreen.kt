package com.example.vivu_application.view.home


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vivu_application.navigation.BottomNavigationBar
import com.example.vivu_application.ui.components.CloudAnimationScreen// Đảm bảo bạn có file này
import com.example.vivu_application.viewmodel.HomeViewModel // Sử dụng HomeViewModel đã cập nhật

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    // Sử dụng HomeViewModel (đã được cập nhật để hỗ trợ phân trang)
    homeViewModel: HomeViewModel = viewModel()
) {
    val hasSetDefaultCategory = remember { mutableStateOf(false) }


    // --- KHÔNG THAY ĐỔI LOGIC STATE ---
    val listUiState by homeViewModel.listUiState.collectAsState()
    val selectedCategory = listUiState.selectedCategory
    // --- KẾT THÚC KHÔNG THAY ĐỔI ---

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)), // Giữ nguyên
        containerColor = Color.Transparent,
        //  contentWindowInsets = WindowInsets(0, 0, 0, 0), // Giữ nguyên: Không bị đẩy bởi bàn phím
        bottomBar = {
            if (!imeVisible) {
                BottomNavigationBar(navController)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // Giữ nguyên: tránh bị che bởi status/nav bar nhưng cho phép vẽ dưới chúng
                // .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
                .windowInsetsPadding(WindowInsets.safeDrawing) // tránh bị che bởi status/nav bar
        ) {
            // Cloud Animation (Giữ nguyên)
            CloudAnimationScreen(
                modifier = Modifier
//                    .offset(y = (-50).dp)
                    .graphicsLayer {
                        translationY = -110.dp.toPx() // tương đương offset
                    }
                    .fillMaxSize()
                    .zIndex(1f),
            )

            // Các nút Category (Cập nhật UI, giữ nguyên logic onClick)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp) // Giữ nguyên padding top cho Box chứa nút
                    .zIndex(2f),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Sử dụng padding horizontal từ file mới cho Row
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterHorizontally) // Giữ nguyên arrangement
                ) {
                    // Nút TOUR (Sử dụng CustomCategoryButton đã cập nhật UI)
                    CustomCategoryButton(
                        text = "TOUR",
                        isSelected = selectedCategory == "tour", // Lấy từ listUiState
                        modifier = Modifier.weight(1f), // Giữ weight để cân bằng
                        onClick = {
                            Log.d("HomeScreen", "TOUR button clicked")
                            homeViewModel.setCategory("tour") // Giữ nguyên logic
                        }
                    )
                    // Nút LOCATION (Sử dụng CustomCategoryButton đã cập nhật UI)
                    CustomCategoryButton(
                        text = "LOCATION",
                        isSelected = selectedCategory == "location", // Lấy từ listUiState
                        modifier = Modifier.weight(1f), // Giữ weight để cân bằng
                        onClick = {
                            Log.d("HomeScreen", "LOCATION button clicked")
                            homeViewModel.setCategory("location") // Giữ nguyên logic
                        }
                    )
                }
            }

            // Phần hiển thị danh sách hoặc loading/error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // --- CẬP NHẬT Ở ĐÂY ---
                    .padding(top = 90.dp) // Cập nhật padding top từ 110.dp -> 90.dp
                    .zIndex(0f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- KHÔNG THAY ĐỔI LOGIC HIỂN THỊ ---
                // Kiểm tra trạng thái từ listUiState
                when {
                    listUiState.isLoading && listUiState.items.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    listUiState.error != null && listUiState.items.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Lỗi: ${listUiState.error}",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { homeViewModel.setCategory(selectedCategory) }) { // Retry
                                    Text("Thử lại")
                                }
                            }
                        }
                    }
                    // Hiển thị danh sách
                    else -> {
                        PostListScreen(
                            navController = navController,
                            homeViewModel = homeViewModel, // Giữ nguyên truyền ViewModel
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                // --- KẾT THÚC KHÔNG THAY ĐỔI ---
            }
        }
    }
}

// CustomCategoryButton (Đã cập nhật UI theo file mới)
@Composable
fun CustomCategoryButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier // Sử dụng modifier được truyền vào (bao gồm weight)
            // --- CẬP NHẬT Ở ĐÂY ---
            // .width(200.dp) // Bỏ width cố định để dùng weight
            .height(30.dp) // Giữ nguyên height
            .padding(horizontal = 10.dp) // Cập nhật padding horizontal
            .then(if (isSelected) Modifier.shadow(10.dp, shape = RoundedCornerShape(40.dp)) else Modifier) // Giữ shadow
            .border(2.dp, Color.Black, RoundedCornerShape(40.dp)) // Giữ border
            .clip(RoundedCornerShape(40.dp)) // Giữ clip
            .background(if (isSelected) Color(0xFFA1C9F1) else Color.Transparent) // Giữ background
            .clickable { onClick() },
        contentAlignment = Alignment.Center // Giữ contentAlignment
    ) {
        Text(
            text = text,
            fontSize = 16.sp, // Giữ fontSize
            fontWeight = FontWeight.Bold, // Giữ fontWeight
            color = Color.Black, // Giữ color
            textAlign = TextAlign.Center // Giữ textAlign
        )
    }
}