package com.example.vivu_application.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.* // Thay đổi import để bao gồm getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel // Giữ nguyên
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.vivu_application.controller.PostController
import com.example.vivu_application.ui.components.SearchViewModel
import com.example.vivu_application.ui.components.TopHeader
import com.example.vivu_application.view.auth.login.ForgotPasswordScreen
import com.example.vivu_application.view.auth.login.LoginScreen
import com.example.vivu_application.view.auth.login.NewPasswordScreen
import com.example.vivu_application.view.auth.login.PasswordRecoveryOTP
import com.example.vivu_application.view.auth.register.CreateNewAccountScreen
import com.example.vivu_application.view.auth.register.CreateNameAndPasswordNewAccount
import com.example.vivu_application.view.auth.register.CreateNewAccountOTP
import com.example.vivu_application.view.chat.ChatScreen
import com.example.vivu_application.view.chat.ChatViewModel
import com.example.vivu_application.view.favorites.FavoritesScreen
import com.example.vivu_application.view.home.HomeScreen
import com.example.vivu_application.view.home.PostDetailRouterScreen
import com.example.vivu_application.view.home.PostListScreen
import com.example.vivu_application.view.onboarding.OnboardingScreen
import com.example.vivu_application.view.onboarding.OnboardingUtils
import com.example.vivu_application.view.profile.detail.*
import kotlin.text.startsWith


@Composable
fun AppNavigation(
    navController: NavHostController,
    postController: PostController,
    onboardingUtils: OnboardingUtils,
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel
) {
    val startDestination = remember(
        onboardingUtils.isOnboardingCompleted(),
        onboardingUtils.isLogIn(),
        onboardingUtils.isAccountCreated()
    ) {
        when {
            !onboardingUtils.isOnboardingCompleted() -> "onboarding"
            !onboardingUtils.isLogIn() -> "login"
            else -> "home"
        }
    }

    val routesWithTopHeader = listOf(
        "home",
        "postList"

    )

    // ViewModel cho TopHeader (Giữ nguyên)
    val searchViewModel: SearchViewModel = viewModel()
    val searchText by searchViewModel.searchText.collectAsState()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
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
        composable("login") {
            LoginScreen(
                navController = navController,
                onboardingUtils = onboardingUtils
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController)
        }
        composable("createAccount") {
            CreateNewAccountScreen(navController = navController)
        }

        composable(
            route = "createNewAccountOTP/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            if (email != null) {
                CreateNewAccountOTP(navController = navController, email = email)
            } else {
                Log.e("AppNavigation", "Email argument missing for createNewAccountOTP route")
                Text("Error: Missing email information.")
            }
        }

        composable(
            route = "createNameAndPasswordAccount/{email}/{otp}", // Phải có cả {email} và {otp}
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") {
                    type = NavType.StringType
                }    // Phải định nghĩa argument "otp"
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val otp = backStackEntry.arguments?.getString("otp")     // Phải lấy argument "otp"

            if (email != null && otp != null) {                     // Phải kiểm tra cả hai
                // Log giá trị nhận được để kiểm tra
                Log.d("AppNavigation", "Received for CreateNameAndPassword: email=$email, otp=$otp")
                CreateNameAndPasswordNewAccount(
                    navController = navController,
                    onboardingUtils = onboardingUtils,
                    email = email,
                    otp = otp      // Truyền cả hai vào Composable
                )
            } else {
                // ... xử lý lỗi thiếu argument ...
                Log.e(
                    "AppNavigation",
                    "Email or OTP argument missing for createNameAndPasswordAccount route"
                )
                Text("Error: Missing required registration information.")
            }
        }

        // ... (Các composable khác giữ nguyên như trước) ...

        composable(
            route = "passwordRecoveryOTP/{email}", // ++ THÊM ARGUMENT ROUTE ++
            arguments = listOf(navArgument("email") { type = NavType.StringType }) // ++ ĐỊNH NGHĨA ARGUMENT ++
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") // ++ LẤY ARGUMENT ++
            if (email != null) {
                // ++ TRUYỀN EMAIL VÀO COMPOSABLE ++
                PasswordRecoveryOTP(navController = navController, email = email)
            } else {
                // Xử lý lỗi nếu email bị thiếu
                Log.e("AppNavigation", "Email argument missing for passwordRecoveryOTP route")
                Text("Error: Missing required information for password recovery.")
                // Có thể navigate back
                // navController.popBackStack()
            }
        }

        composable(
            route = "newPasswordScreen/{email}/{otpOrToken}", // ++ Định nghĩa route mới với 2 arguments ++
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                // Đặt tên là "otpOrToken" vì có thể là OTP hoặc token xác thực
                navArgument("otpOrToken") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val otpOrToken = backStackEntry.arguments?.getString("otpOrToken") // Lấy argument thứ hai

            if (email != null && otpOrToken != null) { // Kiểm tra cả hai
                Log.d("AppNavigation", "Navigating to NewPasswordScreen with email=$email, otpOrToken=$otpOrToken")
                // ++ TRUYỀN CẢ HAI VÀO NewPasswordScreen ++
                NewPasswordScreen(
                    navController = navController,
                    email = email,
                    verificationData = otpOrToken // Truyền dữ liệu xác thực
                )
            } else {
                // Xử lý lỗi thiếu argument
                Log.e("AppNavigation", "Email or otp/token missing for newPasswordScreen route")
                Text("Error: Missing required information for password reset.")
            }
        }




        composable("profilePage") {
            ProfilePage(navController = navController)
        }
        composable("profilePageDetail") {
            ProfilePageDetail(navController = navController)
        }


        composable("profilePageEmail") {
            VerifyCurrentPasswordScreen(navController = navController)
        }



        composable(
            route = "profileEnterNewEmail/{currentPassword}", // Tên route mới + tham số mật khẩu
            arguments = listOf(navArgument("currentPassword") { type = NavType.StringType })
        ) { backStackEntry ->
            val password = backStackEntry.arguments?.getString("currentPassword")
            if (password != null) {
                // Ánh xạ đến Composable ProfileNewEmail và truyền mật khẩu
                ProfileNewEmail(navController = navController, currentPassword = password)
            } else {
                // Xử lý lỗi nếu thiếu mật khẩu
                Log.e("AppNavigation", "Current password missing for profileEnterNewEmail route")
                Text("Error: Missing required information.")
                // Có thể navigate back
                navController.popBackStack()
            }
        }


        composable(
            route = "profileOTPEmail/{newEmail}", // Route này đúng cho ProfileOTPEmail
            arguments = listOf(navArgument("newEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val newEmail = backStackEntry.arguments?.getString("newEmail")
            if (newEmail != null) {
                // Ánh xạ đến ProfileOTPEmail
                ProfileOTPEmail(navController = navController, newEmail = newEmail)
            } else {
                // ... xử lý lỗi thiếu email ...
            }
        }

        composable("profilePagePassword") {
            ProfilePassword(navController = navController)
        }


        composable ("profilePageNewPassword") {
            ProfileNewPassword(navController = navController)
        }

        composable(
            route = "profilePageOTPPassword/{email}", // ++ Đã có hoặc thêm vào ++
            arguments = listOf(navArgument("email") { type = NavType.StringType }) // ++ Đã có hoặc thêm vào ++
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") // ++ Lấy email ++
            if (email != null) {
                // ++ Truyền email vào Composable ++
                ProfileOTPPassword(navController = navController, email = email) // Đảm bảo ProfileOTPPassword nhận email
            } else {
                // Xử lý lỗi thiếu email
                Log.e("AppNavigation", "Email argument missing for profilePageOTPPassword")
                Text("Error: Missing required information.")
            }
        }


        composable(
            route = "profilePageNewPassword2/{email}/{otp}", // Nhận email và otp
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val otp = backStackEntry.arguments?.getString("otp")
            if (email != null && otp != null) {
                // Gọi Composable đã đổi tên (từ ProfileNewPassword2.kt) và truyền email, otp
                ProfileNewPassword2(
                    navController = navController,
                    email = email,
                    otp = otp // Truyền otp vào đây
                )
            } else { /* Xử lý lỗi thiếu argument */ }
        }




        composable("profilePageDetailEdit") {
            ProfilePageDetailEdit(navController = navController)
        }
        // --- Route chính (Giữ nguyên logic ViewModel của file CŨ) ---
        composable("home") {
            // <<< Dùng HomeViewModel >>>
            HomeScreen(navController = navController, homeViewModel = viewModel())
        }
        composable("postList") { // Route này có thể vẫn cần nếu là kết quả search chẳng hạn
            // <<< Dùng HomeViewModel >>>
            PostListScreen(navController = navController, homeViewModel = viewModel())
        }
        composable("favorites") {
            // <<< Dùng PostController (logic cũ) >>>
            FavoritesScreen(navController = navController, postController = postController)
        }
        composable("chat") {
            ChatScreen(
                viewModel = chatViewModel,
                navController = navController
            )
        }

        // --- Xóa route chi tiết postTitle cũ (nếu còn) ---
        // --- Giữ nguyên các route chi tiết tourId và locationId ---
        composable(
            route = "tourDetail/{tourId}", // <<< Route chi tiết Tour >>>
            arguments = listOf(navArgument("tourId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getInt("tourId") ?: -1
            if (tourId != -1) {
                // <<< Dùng PostDetailRouterScreen >>>
                PostDetailRouterScreen(
                    navController = navController,
                    itemId = tourId,
                    itemType = "tour"
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ID Tour không hợp lệ.")
                }
            }
        }

        composable(
            route = "locationDetail/{locationId}", // <<< Route chi tiết Location >>>
            arguments = listOf(navArgument("locationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: -1
            if (locationId != -1) {
                // <<< Dùng PostDetailRouterScreen >>>
                PostDetailRouterScreen(
                    navController = navController,
                    itemId = locationId,
                    itemType = "location"
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ID Location không hợp lệ.")
                }
            }
        }
    } // Kết thúc NavHost

    // --- TopHeader: Đặt bên ngoài NavHost, sử dụng UI từ file MỚI, điều kiện từ file CŨ ---
    // Điều kiện hiển thị giữ nguyên logic file cũ (home, tourDetail, locationDetail)
    val currentRoute = currentDestination?.route

    val showTopHeader = currentRoute == "home" ||
            currentRoute?.startsWith("tourDetail") == true ||
            currentRoute?.startsWith("locationDetail") == true

    if (showTopHeader) {
        TopHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .zIndex(2f),
            searchText = searchText,
            onSearchTextChange = { searchViewModel.onSearchTextChange(it) }
        )
    }



}