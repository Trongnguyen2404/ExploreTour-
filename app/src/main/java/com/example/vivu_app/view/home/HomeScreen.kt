package com.example.vivu_app.view.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.weight
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.navigation.BottomNavigationBar
import com.example.vivu_app.ui.components.CloudAnimationScreen
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsHeight


@Composable
fun HomeScreen(navController: NavHostController, postController: PostController) {
    var selectedCategory by remember { mutableStateOf("location") }

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val postsFromController by postController.posts.collectAsState()
    LaunchedEffect(Unit) {
        postController.setCategory(selectedCategory)
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars, // Không chịu ảnh hưởng bàn phím
        bottomBar = {
            if (!imeVisible) {
                BottomNavigationBar(navController,)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing) // tránh bị che bởi status/nav bar
        ) {
            CloudAnimationScreen(
                modifier = Modifier
//                    .offset(y = (-50).dp)
                    .graphicsLayer {
                        translationY = -110.dp.toPx() // tương đương offset
                    }
                    .fillMaxSize()
                    .zIndex(1f),

            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp)
                    .zIndex(2f),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterHorizontally)
                ) {

                    CustomCategoryButton(
                        text = "TOUR",
                        isSelected = selectedCategory == "tour",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedCategory = "tour"
                            postController.setCategory("tour")
                        }
                    )
                    CustomCategoryButton(
                        text = "LOCATION",
                        isSelected = selectedCategory == "location",
                        modifier = Modifier.weight(1f),
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
                    .padding(top = 90.dp)
                    .zIndex(0f)
            ) {
                //Spacer(modifier = Modifier.height(80.dp))
                PostListScreen(navController, postController)
            }
        }
    }
}

@Composable
fun CustomCategoryButton(text: String, isSelected: Boolean,modifier: Modifier = Modifier, onClick: () -> Unit) {
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