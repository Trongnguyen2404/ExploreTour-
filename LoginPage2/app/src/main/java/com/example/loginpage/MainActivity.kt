package com.example.loginpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.loginpage.controller.PostController
import com.example.loginpage.controller.PostControllerFactory
import com.example.loginpage.navigation.AppNavigation
import com.example.loginpage.ui.theme.OnboardingScreenTheme
import com.example.loginpage.view.chat.ChatViewModel
import com.example.loginpage.view.favorites.PreferencesManager
import com.example.loginpage.view.onboarding.OnboardingUtils

class MainActivity : ComponentActivity() {

    private val onboardingUtils by lazy { OnboardingUtils(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Đặt biểu tượng thanh trạng thái thành màu đen (dark icons)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars =
            true

        // Khởi tạo PreferencesManager
        val preferencesManager = PreferencesManager(applicationContext)

        // Sử dụng ViewModelProvider với Factory
        val postController: PostController = ViewModelProvider(
            this,
            PostControllerFactory(preferencesManager)
        )[PostController::class.java]

        val chatViewModel = ViewModelProvider(this) [ChatViewModel::class.java]

        setContent {
            OnboardingScreenTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        postController = postController,
                        onboardingUtils = onboardingUtils, // Truyền onboardingUtils để kiểm tra trạng thái
                        modifier = Modifier.padding(PaddingValues()),
                        chatViewModel = chatViewModel // Truyền chatViewModel vào AppNavigation
                    )
                }
            }
        }
    }
}