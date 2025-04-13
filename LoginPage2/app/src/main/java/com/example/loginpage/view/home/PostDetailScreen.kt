package com.example.loginpage.view.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.loginpage.controller.PostController
import com.example.loginpage.model.PostType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.loginpage.navigation.BottomNavigationBar
import com.example.loginpage.ui.CloudAnimationScreen


@Composable
fun PostDetailScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    postViewModel: PostController,
) {
    val postTitle = backStackEntry.arguments?.getString("postTitle") ?: ""
    val post = postViewModel.getPostByTitle(postTitle) // Lấy bài viết từ ViewModel

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Tắt tất cả insets
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            CloudAnimationScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f)
            )
            if (post != null) {
                when (post.type) {
                    PostType.TOUR -> TourDetailScreen(post, postViewModel)
                    PostType.LOCATION -> LocationDetailScreen(post, postViewModel)
                    else -> {
                        Text("Loại bài viết không hỗ trợ", color = Color.Red)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bài viết không tồn tại",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Red
                    )
                }
            }
        }
    }
}