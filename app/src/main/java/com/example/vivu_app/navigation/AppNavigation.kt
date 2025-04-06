package com.example.vivu_app.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vivu_app.ui.CloudAnimationScreen // Import màn hình mây
import com.example.vivu_app.view.home.HomeScreen
import com.example.vivu_app.view.favorites.FavoritesScreen
import com.example.vivu_app.view.chat.ChatScreen
import com.example.vivu_app.view.profile.ProfileScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.vivu_app.R
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.view.posts.PostListScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vivu_app.view.home.PostDetailScreen


@Composable
fun AppNavigation(
    navController: NavHostController,
    postController: PostController
) {
    val posts by postController.posts.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {

        CloudAnimationScreen(modifier = Modifier.fillMaxSize().zIndex(0f))

        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController, postController) }

            composable("postList") { PostListScreen(navController, postController) }

            composable("favorites") { FavoritesScreen(navController, postController) }

            composable("chat") { ChatScreen(navController) }

            composable("profile") { ProfileScreen(navController) }

            composable(
                route = "postDetail/{postTitle}",
                arguments = listOf(navArgument("postTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                PostDetailScreen(navController, backStackEntry, postController)
            }
        }

        if (currentDestination != "favorites") {
            TopHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .zIndex(2f)
            )
        }
    }
}

@Composable
fun TopHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(top = 15.dp)
            .zIndex(2f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vivu),
            contentDescription = "VIVU Logo",
            modifier = Modifier
                .size(130.dp)
                .offset(y = (-20).dp),
        )

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Tên của bạn",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .offset(y = 5.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            SearchBar()
        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()  //  Chiếm toàn bộ chiều rộng có thể
            .height(40.dp) //  Tăng chiều cao một chút
            .background(Color.White, shape = RoundedCornerShape(50))
            .border(2.dp, Color.Black, shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 50.dp, top = 8.dp, bottom = 8.dp) // Tăng padding bên trái
        )

        if (searchText.isEmpty()) {
            Text(
                text = "Search...",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp) // Giữ text placeholder căn trái
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            tint = Color.Unspecified, // Giữ màu gốc của icon
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(35.dp) // Tăng kích thước icon lớn hơn
                .padding(end = 12.dp) // Tạo khoảng trống giữa icon và cạnh phải
        )
    }
}