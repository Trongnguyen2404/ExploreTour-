package com.example.vivu_app.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.navigation.BottomNavigationBar
import com.example.vivu_app.ui.components.CloudAnimationScreen
import com.example.vivu_app.view.posts.PostListScreen
import androidx.navigation.NavHostController




@Composable
fun HomeScreen(navController: NavHostController, postController: PostController) {
    var selectedCategory by remember { mutableStateOf("tour") }

    LaunchedEffect(Unit) {
        postController.setCategory("tour")
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CloudAnimationScreen(
                modifier = Modifier
                    .offset(y = (-50).dp)
                    .fillMaxSize()
                    .zIndex(0f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 105.dp)
                    .zIndex(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    CustomCategoryButton(
                        text = "TOUR",
                        isSelected = selectedCategory == "tour",
                        onClick = {
                            selectedCategory = "tour"
                            postController.setCategory("tour")
                        }
                    )
                    CustomCategoryButton(
                        text = "LOCATION",
                        isSelected = selectedCategory == "location",
                        onClick = {
                            selectedCategory = "location"
                            postController.setCategory("location")
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 150.dp)
            ) {
                PostListScreen(navController, postController)
            }
        }
    }
}

@Composable
fun CustomCategoryButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(200.dp) // Độ rộng của viền ngoài
            .height(30.dp) // Độ cao của viền ngoài
            .padding(horizontal = 10.dp) // Cách viền điện thoại 2 bên
            .then(if (isSelected) Modifier.shadow(10.dp, shape = RoundedCornerShape(40.dp)) else Modifier) // Chỉ đổ bóng khi được chọn
            .border(2.dp, Color.Black, RoundedCornerShape(40.dp)) // Viền đen
            .clip(RoundedCornerShape(40.dp)) // Bo góc 40dp
            .background(if (isSelected) Color(0xFFA1C9F1) else Color.Transparent) // Xanh khi chọn, trong suốt khi không chọn
            .clickable { onClick() },
        contentAlignment = Alignment.Center //  text nằm giữa cả chiều ngang & dọc
    ) {
        Text(
            text = text,
            fontSize = 16.sp, // Cỡ chữ 16
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}