package com.example.vivu_app.view.favorites

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_app.R
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.navigation.BottomNavigationBar


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FavoritesScreen(navController: NavController, postController: PostController) {
    // Khai báo biến favoritePosts bằng derivedStateOf từ PostController
//    val favoritePosts by remember {
//        derivedStateOf {
//            postController.posts.value.filter { it.id in postController.favoritePostIds.value }
//        }
//    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_favorites),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-45).dp)
                    .height(130.dp)
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
                        .offset(y = (-35).dp)
                        .align(Alignment.TopStart)
                        .padding(start = 20.dp)
                        .zIndex(1f)
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


                Text(
                    "Không có bài viết yêu thích nào.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(favoritePosts) { post ->
//                        PostItem(
//                            post = post,
//                            navController = navController,
//                            postController = postController,
//                            onFavoriteClick = {
//                                postController.toggleFavorite(post.id)
//                            }
//                        )
//                    }
//                }
//            }
        }
    }
}
