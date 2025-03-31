package com.example.vivu_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.controller.PostControllerFactory
import com.example.vivu_app.navigation.AppNavigation
import com.example.vivu_app.preferences.PreferencesManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


