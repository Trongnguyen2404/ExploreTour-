package com.example.vivu_app.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vivu_app.controller.PostViewModel
import com.example.vivu_app.navigation.BottomNavigationBar
import com.example.vivu_app.ui.CloudAnimationScreen
import com.example.vivu_app.view.posts.PostListScreen

@Composable
fun HomeScreen(navController: NavController, postViewModel: PostViewModel) { // ✅ Nhận postViewModel
    val postViewModel: PostViewModel = viewModel()
    var selectedCategory by remember { mutableStateOf("tour") } // 🔹 Mặc định chọn "TOUR"

    // ✅ Khi mở HomeScreen, tự động load danh sách "TOUR"
    LaunchedEffect(Unit) {
        postViewModel.setCategory("tour")
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 🌥️ Màn hình mây (luôn nằm dưới)
            CloudAnimationScreen(
                modifier = Modifier
                    .offset(y = (-25).dp)
                    .fillMaxSize()
                    .zIndex(0f) // Đảm bảo mây ở dưới
            )


            // 🚀 Nút "TOUR" & "LOCATION" nổi trên mây
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 130.dp) // Điều chỉnh vị trí theo nhu cầu
                    .zIndex(1f), // Đưa lên trên mây
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    CustomCategoryButton(
                        text = "TOUR",
                isSelected = selectedCategory == "tour",
                onClick = {
                    selectedCategory = "tour"
                    postViewModel.setCategory("tour")
                }
                )
                CustomCategoryButton(
                    text = "LOCATION",
                    isSelected = selectedCategory == "location",
                    onClick = {
                        selectedCategory = "location"
                        postViewModel.setCategory("location")
                    }
                )
                }
            }

            // 📝 Danh sách bài viết ở dưới nút
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 170.dp) // Đẩy danh sách xuống dưới nút
            ) {
                PostListScreen(navController, postViewModel)
            }
        }
    }
}

@Composable
fun CustomCategoryButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(200.dp) // 📏 Độ rộng của viền ngoài
            .height(30.dp) // 📏 Độ cao của viền ngoài
            .padding(horizontal = 10.dp) // 🔹 Cách viền điện thoại 2 bên
            .then(if (isSelected) Modifier.shadow(10.dp, shape = RoundedCornerShape(40.dp)) else Modifier) // ✨ Chỉ đổ bóng khi được chọn
            .border(2.dp, Color.Black, RoundedCornerShape(40.dp)) // 🔹 Viền đen
            .clip(RoundedCornerShape(40.dp)) // 🟢 Bo góc 40dp
            .background(if (isSelected) Color(0xFFA1C9F1) else Color.Transparent) // 🔹 Xanh khi chọn, trong suốt khi không chọn
            .clickable { onClick() },
        contentAlignment = Alignment.Center // 🔹 Đảm bảo text nằm giữa cả chiều ngang & dọc
    ) {
        Text(
            text = text,
            fontSize = 16.sp, // 🔹 Cỡ chữ 16
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}




