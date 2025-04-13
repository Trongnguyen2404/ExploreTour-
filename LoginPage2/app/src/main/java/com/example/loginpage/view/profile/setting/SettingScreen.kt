package com.example.loginpage.view.profile.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.loginpage.R

@Composable
fun SettingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFececec))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nút quay lại
        Row(

            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                text = "Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Terms of service
        SettingItem(
            icon = R.drawable.term_of_service_icon, // Thay bằng icon của bạn
            title = "Terms of service",
            onClick = { navController.navigate("termsOfService") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy policy
        SettingItem(
            icon = R.drawable.privacy_policy_icon, // Thay bằng icon của bạn
            title = "Privacy policy",
            onClick = { navController.navigate("privacyPolicy") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Introduction
        SettingItem(
            icon = R.drawable.introduction_icon, // Thay bằng icon của bạn
            title = "Introduction",
            onClick = { navController.navigate("introduction") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Log out
        SettingItem(
            icon = R.drawable.logout_icon, // Thay bằng icon của bạn
            title = "Log out",
            iconTint = Color(0xFF00C4B4), // Màu xanh lam cho icon Log out
            onClick = { navController.navigate("login") }
        )
    }
}

@Composable
fun SettingItem(
    icon: Int,
    title: String,
    iconTint: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.navigate_next),
            contentDescription = "Navigate",
            modifier = Modifier.size(24.dp)
        )
    }
}