package com.example.vivu_app.view.posts

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.vivu_app.controller.PostViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PostDetailScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    postViewModel: PostViewModel
){
    val postTitle = backStackEntry.arguments?.getString("postTitle") ?: ""
    val post = postViewModel.getPostByTitle(postTitle) // ✅ Lấy bài viết từ ViewModel

    if (post != null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Image(
                painter = painterResource(id = post.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = post.title, style = MaterialTheme.typography.titleLarge)
            Text(text = "⭐ ${post.rating}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại")
            }
        }
    } else {
        Text("Bài viết không tồn tại", style = MaterialTheme.typography.titleLarge)
    }
}
