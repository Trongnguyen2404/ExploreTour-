package com.example.vivu_app.navigation

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
import com.example.vivu_app.controller.PostViewModel
import com.example.vivu_app.view.posts.PostDetailScreen
import com.example.vivu_app.view.posts.PostListScreen
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel





@Composable
fun AppNavigation(navController: NavHostController) {

    // ✅ Khởi tạo ViewModel
    val postViewModel: PostViewModel = viewModel() // ✅ Khởi tạo ViewModel
    val posts by postViewModel.posts.collectAsState(initial = emptyList()) // ✅ Lấy danh sách bài viết từ ViewModel



    Box(modifier = Modifier.fillMaxSize()) {
        // 🌥️ Đám mây (luôn nằm dưới)
        CloudAnimationScreen(modifier = Modifier.fillMaxSize().zIndex(0f))

        // 🏠 Điều hướng màn hình
        NavHost(
            navController = navController,
            startDestination = "home"
        )
        {
            composable(route = "home") {
                HomeScreen(navController, postViewModel) // ✅ Truyền ViewModel vào HomeScreen
            }
            composable(route = "postList") {
                PostListScreen(navController, postViewModel) // ✅ Truyền ViewModel vào PostListScreen
            }

            composable("home") {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(navController , postViewModel)
//                    CloudAnimationScreen(modifier = Modifier.zIndex(0f)) // ✅ Đặt zIndex cao hơn

                }
            }
            composable("favorites") {
                Box(modifier = Modifier.fillMaxSize()) {
                    FavoritesScreen(navController)
                }
            }
            composable("chat") {
                Box(modifier = Modifier.fillMaxSize()) {
                    ChatScreen(navController)

                }
            }
            composable("profile") {
                Box(modifier = Modifier.fillMaxSize()) {
                    ProfileScreen(navController)
                }
            }


            // 🆕 Màn hình danh sách bài viết
            composable("postList") {
                PostListScreen(navController, postViewModel) // ✅ Đã truyền danh sách `posts`
            }

            // 🆕 Màn hình chi tiết bài viết
            composable(
                "postDetail/{postTitle}",
                arguments = listOf(navArgument("postTitle") { type = NavType.StringType })
            ) { backStackEntry ->
                PostDetailScreen(navController, backStackEntry, postViewModel) // ✅ Đúng, truyền ViewModel
            }

        }
        // 🔍 Header nổi trên mây
        TopHeader(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .zIndex(2f)
        )

        }
    }

@Composable
fun TopHeader(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = modifier
//            .fillMaxWidth()
            .padding(top = 15.dp) // Căn lề cho đẹp
            .zIndex(2f),
        horizontalArrangement = Arrangement.SpaceBetween, // Căn logo bên trái, cột avatar + search bên phải
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_vivu),
            contentDescription = "VIVU Logo",
            modifier = Modifier
                .size(130.dp)  // Thử kích thước lớn hơn
                .offset(y = (-20).dp), // Đẩy lên trên 20dp, sang trái 10dp
            )

        //  CỘT BÊN PHẢI: Chứa (Tên + Avatar) & (Thanh Tìm Kiếm)
        Column(
            horizontalAlignment = Alignment.End // Căn avatar về bên phải
        ) {
            // Avatar + Tên (BÊN TRÊN)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tên của bạn",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp) // Khoảng cách giữa tên & avatar
                )

                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(10.dp)) // Khoảng cách giữa avatar và thanh tìm kiếm


            // Thanh tìm kiếm
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
            .border(1.dp, Color.Black, shape = RoundedCornerShape(50)),
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

