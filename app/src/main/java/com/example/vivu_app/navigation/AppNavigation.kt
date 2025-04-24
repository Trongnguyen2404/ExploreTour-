package com.example.vivu_app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vivu_app.view.home.HomeScreen
import com.example.vivu_app.view.favorites.FavoritesScreen
import com.example.vivu_app.view.chat.ChatScreen
import com.example.vivu_app.view.profile.ProfileScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.vivu_app.controller.PostController
import com.example.vivu_app.view.home.PostListScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vivu_app.ui.components.SearchViewModel
import com.example.vivu_app.view.home.PostDetailScreen
import com.example.vivu_app.ui.components.TopHeader
import com.example.vivu_app.view.home.TourDetailScreen
import kotlinx.coroutines.delay


@Composable
fun AppNavigation(
    navController: NavHostController,
    postController: PostController
) {
    val posts by postController.posts.collectAsState(initial = emptyList())



    Box(modifier = Modifier.fillMaxSize()) {

//        CloudAnimationScreen(modifier = Modifier.fillMaxSize().zIndex(0f))

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


        val viewModel: SearchViewModel = viewModel()
        val searchText = viewModel.searchText

        if (currentDestination == "home" || currentDestination?.startsWith("postDetail") == true) {
            TopHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .zIndex(2f),
                searchText = searchText,
                onSearchTextChange = { viewModel.onSearchTextChange(it) }
            )
        }

    }
}