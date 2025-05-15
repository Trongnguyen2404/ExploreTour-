package com.example.vivu_application.navigation


import androidx.annotation.DrawableRes

data class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int
)