package com.example.vivu_app.view.home


import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.model.PostType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
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

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0



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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),

        ) {
            CloudAnimationScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = -50.dp.toPx() // tương đương offset
                    }
                    .zIndex(2f),
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
