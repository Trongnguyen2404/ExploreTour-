package com.example.vivu_app.view.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.vivu_app.navigation.BottomNavigationBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun FavoritesScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Text("Màn hình Yêu thích", modifier = Modifier.padding(16.dp))
        }
    }
}
