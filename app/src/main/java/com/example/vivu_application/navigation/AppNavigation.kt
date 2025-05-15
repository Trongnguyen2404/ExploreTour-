package com.example.vivu_application.navigation
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
import com.example.vivu_application.view.profile.setting.SettingScreen
import com.example.vivu_application.viewmodel.FavoriteViewModel
import com.example.vivu_application.viewmodel.HomeViewModel
import kotlin.text.startsWith
import androidx.compose.runtime.collectAsState // Đảm bảo import này
import androidx.compose.runtime.getValue      // Đảm bảo import này
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

    val homeViewModel: HomeViewModel = viewModel()
    val searchQuery by homeViewModel.searchQuery.collectAsState() // <<< LẤY STATE
    val userProfile by homeViewModel.userProfile.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
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
                CreateNameAndPasswordNewAccount(
                    navController = navController,
                    onboardingUtils = onboardingUtils,
                    email = email,
                    otp = otp      // Truyền cả hai vào Composable
                )
            }
        }

        // ... (Các composable khác giữ nguyên như trước) ...

        composable(
            route = "passwordRecoveryOTP/{email}", // ++ THÊM ARGUMENT ROUTE ++
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
            }) // ++ ĐỊNH NGHĨA ARGUMENT ++
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") // ++ LẤY ARGUMENT ++
            if (email != null) {
                // ++ TRUYỀN EMAIL VÀO COMPOSABLE ++
                PasswordRecoveryOTP(navController = navController, email = email)
            } else {
                // Xử lý lỗi nếu email bị thiếu
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
            val otpOrToken =
                backStackEntry.arguments?.getString("otpOrToken") // Lấy argument thứ hai

            if (email != null && otpOrToken != null) { // Kiểm tra cả hai
                // ++ TRUYỀN CẢ HAI VÀO NewPasswordScreen ++
                NewPasswordScreen(
                    navController = navController,
                    email = email,
                    verificationData = otpOrToken // Truyền dữ liệu xác thực
                )
            } else {
                // Xử lý lỗi thiếu argument
                Text("Error: Missing required information for password reset.")
            }
        }




        composable("profilePage") {
            ProfilePage(navController = navController)
        }

        composable("settingScreen") {
            SettingScreen(navController = navController, onboardingUtils)
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
                Text("Error: Missing required information.")
                // Có thể navigate back
                navController.popBackStack()
            }
        }


        composable(
            route = "profileOTPEmail/{newEmail}",
            // ... arguments ...
        ) { backStackEntry ->
            val newEmail = backStackEntry.arguments?.getString("newEmail")
            if (newEmail != null) {
                ProfileOTPEmail(
                    navController = navController,
                    newEmail = newEmail,
                    onboardingUtils = onboardingUtils // ++ TRUYỀN onboardingUtils VÀO ĐÂY ++
                )
            } else {
                // ... xử lý lỗi thiếu email ...
            }
        }

        composable("profilePagePassword") {
            ProfilePassword(navController = navController)
        }


        composable("profilePageNewPassword") {
            ProfileNewPassword(navController = navController)
        }

        composable(
            route = "profilePageOTPPassword/{email}", // ++ Đã có hoặc thêm vào ++
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
            }) // ++ Đã có hoặc thêm vào ++
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") // ++ Lấy email ++
            if (email != null) {
                // ++ Truyền email vào Composable ++
                ProfileOTPPassword(
                    navController = navController,
                    email = email
                ) // Đảm bảo ProfileOTPPassword nhận email
            } else {
                // Xử lý lỗi thiếu email
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
            } else { /* Xử lý lỗi thiếu argument */
            }
        }




        composable("profilePageDetailEdit") {
            ProfilePageDetailEdit(navController = navController)
        }
        composable("home") { navBackStackEntry -> // Đổi tên để tránh nhầm lẫn
            val currentLifecycleState = navBackStackEntry.lifecycle.currentState
            val isResumed = currentLifecycleState == androidx.lifecycle.Lifecycle.State.RESUMED

            // State để đảm bảo chỉ fetch một lần khi resume (tránh fetch liên tục nếu trạng thái không đổi)
            var didFetchOnResume by remember { mutableStateOf(false) }

            LaunchedEffect(isResumed) {
                if (isResumed && !didFetchOnResume) {
                    homeViewModel.fetchUserProfile() // Gọi hàm fetch
                    homeViewModel.refreshFavoriteStatus()
                    didFetchOnResume = true
                } else if (!isResumed) {
                    // Reset cờ khi màn hình không còn resumed
                    didFetchOnResume = false
                }
            }
            // Truyền homeViewModel đã lấy ở trên vào HomeScreen
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }

        composable("postList") {
            PostListScreen(navController = navController, homeViewModel = homeViewModel)
        }
        composable("favorites") {
            // Dùng FavoriteViewModel mới
            val favoriteViewModel: FavoriteViewModel = viewModel()
            FavoritesScreen(navController = navController, favoriteViewModel = favoriteViewModel)
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
            route = "tourDetail/{tourId}",
            arguments = listOf(navArgument("tourId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getInt("tourId") ?: -1
            if (tourId != -1) {
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
            route = "locationDetail/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: -1
            if (locationId != -1) {
                PostDetailRouterScreen( // Composable này chứa LocationDetailScreenContent
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

    val shouldShowEntireTopHeader =
        currentRoute == "home" // Chỉ hiển thị toàn bộ TopHeader (bao gồm SearchBar) ở màn hình home

    val shouldShowBaseTopHeader = currentRoute == "home" ||
            currentRoute?.startsWith("tourDetail") == true ||
            currentRoute?.startsWith("locationDetail") == true

    if (shouldShowBaseTopHeader) {
        TopHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp) // Giảm padding vertical một chút
                .zIndex(2f),
            searchText = searchQuery,
            onSearchTextChange = { newQuery ->
                homeViewModel.onSearchQueryChanged(newQuery)
            },
            userName = userProfile?.name,
            avatarUrl = userProfile?.profilePictureUrl,
            // ++ TRUYỀN GIÁ TRỊ CHO showSearchBar ++
            // SearchBar chỉ hiển thị nếu shouldShowEntireTopHeader là true (tức là đang ở màn hình "home")
            showSearchBar = shouldShowEntireTopHeader
        )
    }
}