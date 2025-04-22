package com.example.vivu_app.view.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.vivu_app.navigation.BottomNavigationBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun ProfileScreen(navController: NavController) {
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars, // Không chịu ảnh hưởng bàn phím
        bottomBar = {
            if (!imeVisible) {
                BottomNavigationBar(navController)
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Text("Màn hình Cá nhân", modifier = Modifier.padding(16.dp))
        }
    }
}