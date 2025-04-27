package com.example.vivu_application.view.profile.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.view.onboarding.OnboardingUtils
import com.example.vivu_application.R
@Composable
fun SettingScreen(navController: NavHostController, onboardingUtils: OnboardingUtils) {

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Log out",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "Do you want to log out?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            // Sửa phần này để căn giữa 2 nút
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            showDialog = false
                            onboardingUtils.setLogOut()
                            navController.navigate("login") {
                                popUpTo("settingScreen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA1C9F1),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Log out")
                    }
                }
            },
            dismissButton = {}
        )
    }


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
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color(0xFF00BCD4)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Terms of service
        SettingItem(
            icon = R.drawable.term_of_service_icon,
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
            icon = Icons.Filled.ExitToApp, // Sử dụng Material Icon cho Log out
            title = "Log out",
            iconTint = Color(0xFF00C4B4),
            onClick = { showDialog = true }
        )
    }
}

@Composable
fun SettingItem(
    icon: Any, // Thay đổi kiểu của icon thành Any để chấp nhận cả Painter và ImageVector
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
        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        } else if (icon is Int) { // Kiểm tra nếu icon là một Int (resource ID)
            Icon(
                painter = painterResource(id = icon), // Chuyển Int thành Painter
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        }
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