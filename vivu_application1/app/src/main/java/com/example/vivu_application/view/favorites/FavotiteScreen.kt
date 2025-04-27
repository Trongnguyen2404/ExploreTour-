package com.example.vivu_application.view.favorites


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_application.controller.PostController
import com.example.vivu_application.navigation.BottomNavigationBar
import com.example.vivu_application.R

@Composable
fun FavoritesScreen(navController: NavController, postController: PostController) {
    val favoritePosts by postController.favoritePosts.collectAsState(initial = emptyList())
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // Thêm padding theo insets hệ thống
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        bottomBar = {
            BottomNavigationBar(navController)
        },
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_favorites),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp + statusBarHeight) // cộng thêm chiều cao status bar
                    .graphicsLayer {
                        translationY = -statusBarHeight.toPx() // kéo ngược lên
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
//                        .offset(y = (-35).dp)
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

            if (favoritePosts.isEmpty()) {
                Text(
                    "Không có bài viết yêu thích nào.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp)
                ) {
                    items(favoritePosts) { post ->
//                        PostItem(
//                            post = post,
//                            navController = navController,
//                            postController = postController,
//                            onFavoriteClick = {
//                                postController.toggleFavorite(post.id)
//                            }
//                        )
                    }
                }
            }
        }
    }
}