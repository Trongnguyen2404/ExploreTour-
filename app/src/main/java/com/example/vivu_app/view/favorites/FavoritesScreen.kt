package com.example.vivu_app.view.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_app.R
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.view.posts.PostItem
import com.example.vivu_app.navigation.BottomNavigationBar

@Composable
fun FavoritesScreen(navController: NavController, postController: PostController) {
    val favoritePosts by postController.favoritePosts.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
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
                        PostItem(
                            post = post,
                            navController = navController,
                            postController = postController,
                            onFavoriteClick = {
                                postController.toggleFavorite(post.id)
                            }
                        )
                    }
                }
            }
        }
    }
}