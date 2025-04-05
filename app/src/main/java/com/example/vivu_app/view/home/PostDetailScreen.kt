package com.example.vivu_app.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.model.PostType
import com.example.vivu_app.view.posts.TourDetailScreen


@Composable
fun PostDetailScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    postViewModel: PostController,
) {
    val postTitle = backStackEntry.arguments?.getString("postTitle") ?: ""
    val post = postViewModel.getPostByTitle(postTitle) // Lấy bài viết từ ViewModel
    if (post != null) {
        when (post.type) {
            PostType.TOUR -> TourDetailScreen(post, navController)
            PostType.LOCATION -> LocationDetailScreen(post, navController)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Bài viết không tồn tại", style = MaterialTheme.typography.titleLarge, color = Color.Red)
        }
    }
}
