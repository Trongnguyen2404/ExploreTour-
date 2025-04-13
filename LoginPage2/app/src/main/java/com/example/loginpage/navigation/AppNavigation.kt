package com.example.loginpage.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.loginpage.R
import com.example.loginpage.controller.PostController
import com.example.loginpage.view.auth.login.ForgotPasswordScreen
import com.example.loginpage.view.auth.login.LoginScreen
import com.example.loginpage.view.auth.login.NewPasswordScreen
import com.example.loginpage.view.auth.login.PasswordRecoveryOTP
import com.example.loginpage.view.auth.register.CreateNewAccountScreen
import com.example.loginpage.view.auth.register.NameInCreateNewAccount
import com.example.loginpage.view.chat.ChatScreen
import com.example.loginpage.view.chat.ChatViewModel
import com.example.loginpage.view.favorites.FavoritesScreen
import com.example.loginpage.view.home.HomeScreen
import com.example.loginpage.view.home.PostDetailScreen
import com.example.loginpage.view.home.PostListScreen
import com.example.loginpage.view.onboarding.OnboardingScreen
import com.example.loginpage.view.onboarding.OnboardingUtils
import com.example.loginpage.view.profile.detail.*
import com.example.loginpage.view.profile.setting.SettingScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    postController: PostController,
    onboardingUtils: OnboardingUtils, // Nhận onboardingUtils từ MainActivity
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel
) {
    // Xác định điểm bắt đầu dựa trên trạng thái onboarding và đăng nhập
    val startDestination = when {
        !onboardingUtils.isOnboardingCompleted() -> "onboarding"
        !onboardingUtils.isLogIn() -> "login"
        else -> "home"
    }

    // Danh sách các route mà TopHeader sẽ hiển thị
    val routesWithTopHeader = listOf(
        "home",
        "postList",
//        "chat",
        "profile"
    )

    // Lấy route hiện tại
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(
                navController = navController,
                onFinished = {
                    onboardingUtils.setOnboardingCompleted()
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("login") { // Đổi từ "loginPage" thành "login" để đồng bộ
            LoginScreen(
                navController = navController,
                onboardingUtils = onboardingUtils // Truyền onboardingUtils vào LoginScreen
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController)
        }
        composable("passwordRecoveryOTP") {
            PasswordRecoveryOTP(navController = navController)
        }
        composable("newPasswordScreen") {
            NewPasswordScreen(navController = navController)
        }
        composable("createAccount") {
            CreateNewAccountScreen(navController = navController)
        }
        composable("createUsername") {
            NameInCreateNewAccount(navController = navController)
        }
        composable("profilePage") {
            ProfilePage(navController = navController)
        }
        composable("profilePageDetail") {
            ProfilePageDetail(navController = navController)
        }
        composable("profilePageEmail") {
            ProfileEmail(navController = navController)
        }
        composable("profilePageNewEmail") {
            ProfileNewEmail(navController = navController)
        }
        composable("profilePagePassword") {
            ProfilePassword(navController = navController)
        }
        composable("profilePageOTPPassword") {
            ProfileOTPPassword(navController = navController)
        }
        composable("profilePageNewPassword") {
            ProfileNewPassword(navController = navController)
        }
        composable("settingScreen") {
            SettingScreen(navController = navController)
        }
        composable("profilePageDetailEdit") {
            ProfilePageDetailEdit(navController = navController)
        }
        composable("home") {
            HomeScreen(navController, postController)
        }
        composable("postList") {
            PostListScreen(navController, postController)
        }
        composable("favorites") {
            FavoritesScreen(navController, postController)
        }
        composable("chat") {
            ChatScreen(
                modifier = modifier,
                viewModel = chatViewModel, // Đảm bảo tên tham số khớp
                navController = navController
            )
        }
        composable(
            route = "postDetail/{postTitle}",
            arguments = listOf(navArgument("postTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            PostDetailScreen(navController, backStackEntry, postController)
        }
    }

    // Chỉ hiển thị TopHeader cho các route được chỉ định
    if (currentDestination in routesWithTopHeader) {
        TopHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .zIndex(2f)
        )
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
            .fillMaxWidth()
            .height(40.dp)
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
                .padding(start = 20.dp, end = 50.dp, top = 8.dp, bottom = 8.dp)
        )

        if (searchText.isEmpty()) {
            Text(
                text = "Search...",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(35.dp)
                .padding(end = 12.dp)
        )
    }
}

@Composable
fun ProfileScreen(navController: NavHostController) {
    TODO("Not yet implemented")
}