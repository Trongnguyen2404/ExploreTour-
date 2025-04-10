package com.example.vivu_app.view.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.model.PostType
import com.example.vivu_app.view.posts.TourDetailScreen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vivu_app.controller.PostControllerFactory
import com.example.vivu_app.navigation.BottomNavigationBar
import com.example.vivu_app.data.local.PreferencesManager
import com.example.vivu_app.ui.components.CloudAnimationScreen


@Composable
fun PostDetailScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    postViewModel: PostController,
) {
    val context = LocalContext.current

    // Tạo PreferencesManager
    val preferencesManager = remember { PreferencesManager(context) }

    // Tạo ViewModel bằng factory
    val postViewModel: PostController = viewModel(
        factory = PostControllerFactory(preferencesManager)
    )
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
                when (post!!.type) {
                    PostType.TOUR -> TourDetailScreen(postId = post!!.id, postViewModel)
                    PostType.LOCATION -> LocationDetailScreen(postId = post!!.id, postViewModel)
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
