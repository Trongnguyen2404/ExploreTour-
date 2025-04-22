package com.example.vivu_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.controller.PostControllerFactory
import com.example.vivu_app.navigation.AppNavigation
import com.example.vivu_app.data.local.PreferencesManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cho phép ứng dụng vẽ dưới các thanh hệ thống (status bar, navigation bar)
        WindowCompat.setDecorFitsSystemWindows(window, false) // <-- Thêm dòng này

        //Khởi tạo PreferencesManager
        val preferencesManager = PreferencesManager(applicationContext)

        //Sử dụng ViewModelProvider với Factory
        val postController: PostController = ViewModelProvider(
            this,
            PostControllerFactory(preferencesManager)
        )[PostController::class.java]



        setContent {
            val navController = rememberNavController()
            AppNavigation(navController = navController, postController = postController)
        }
    }
}