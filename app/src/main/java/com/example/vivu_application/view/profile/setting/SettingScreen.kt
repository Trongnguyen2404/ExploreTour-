package com.example.vivu_application.view.profile.setting

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.view.onboarding.OnboardingUtils
import com.example.vivu_application.R
import com.example.vivu_application.data.local.TokenManager
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(navController: NavHostController, onboardingUtils: OnboardingUtils) {

    var showLogoutDialog by remember { mutableStateOf(false) } // Đổi tên dialog state
    var isLoadingLogout by remember { mutableStateOf(false) } // State loading khi logout

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService

    // --- Hàm xử lý logout ---
    fun logoutUser() {
        isLoadingLogout = true // Bắt đầu loading
        coroutineScope.launch {
            try {
                Log.d("SettingScreen", "Attempting to call logout API...")
                // Gọi API logout (Cần Interceptor thêm token)
                val response = authApiService.logout()

                if (response.isSuccessful) {
                    Log.i("SettingScreen", "Logout API call successful.")
                    // Xử lý thành công (dù API thành công hay không, vẫn nên logout ở client)
                } else {
                    // Lỗi từ API logout (có thể không quan trọng lắm, vẫn logout ở client)
                    val errorBody = response.errorBody()?.string() ?: "Logout API failed."
                    Log.w("SettingScreen", "Logout API call failed: ${response.code()} - $errorBody")
                    // Có thể hiển thị Toast nếu muốn, nhưng thường không cần chặn việc logout client
                    // Toast.makeText(context, "Logout request failed on server.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Lỗi mạng khi gọi API logout
                Log.e("SettingScreen", "Logout API exception: ${e.message}", e)
                // Thường vẫn nên tiếp tục logout ở client
                // Toast.makeText(context, "Network error during logout.", Toast.LENGTH_SHORT).show()
            } finally {
                // --- Thực hiện các bước logout phía Client ---
                Log.d("SettingScreen", "Performing client-side logout.")
                TokenManager.clearTokens() // Xóa token đã lưu
                onboardingUtils.setLogOut() // Cập nhật trạng thái đã logout
                isLoadingLogout = false // Kết thúc loading
                showLogoutDialog = false // Đóng dialog

                // Điều hướng về màn hình Login và xóa hết backstack cũ
                navController.navigate("login") {
                    // Xóa tất cả các màn hình trước đó khỏi backstack
                    popUpTo(navController.graph.startDestinationId) { // Hoặc một route gốc ổn định khác
                        inclusive = true
                    }
                    // Đảm bảo chỉ có một instance của màn hình Login
                    launchSingleTop = true
                }
                Log.d("SettingScreen", "Navigated to login screen.")
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoadingLogout) showLogoutDialog = false }, // Không cho dismiss khi đang loading
            title = { Text("Log out", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = { Text("Are you sure you want to log out?", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Nút Cancel
                    TextButton(
                        onClick = { if (!isLoadingLogout) showLogoutDialog = false },
                        enabled = !isLoadingLogout // Vô hiệu hóa khi đang loading
                    ) {
                        Text("Cancel")
                    }
                    // Nút Logout (xác nhận)
                    Button(
                        onClick = { if (!isLoadingLogout) logoutUser() }, // Gọi hàm logoutUser
                        enabled = !isLoadingLogout, // Vô hiệu hóa khi đang loading
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC3545), // Màu đỏ cảnh báo
                            contentColor = Color.White
                        )
                    ) {
                        // Hiển thị loading hoặc text
                        if (isLoadingLogout) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Log out")
                        }
                    }
                }
            },
            dismissButton = {} // Không cần nút dismiss riêng
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
            onClick = {
                val url = "https://hillaryjunia.github.io/vivu-app-info-pages/terms.html" // << THAY BẰNG URL THỰC TẾ CỦA BẠN
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Xử lý trường hợp không có trình duyệt hoặc lỗi khi mở URL
                    Log.e("SettingScreen", "Could not open URL: $url", e)
                    // Toast.makeText(context, "Could not open link.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy policy
        SettingItem(
            icon = R.drawable.privacy_policy_icon,
            title = "Privacy policy",
            onClick = {
                val url = "https://hillaryjunia.github.io/vivu-app-info-pages/privacy.html" // << THAY BẰNG URL THỰC TẾ CỦA BẠN
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("SettingScreen", "Could not open URL: $url", e)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Introduction
        SettingItem(
            icon = R.drawable.introduction_icon,
            title = "Introduction",
            onClick = {
                val url = "https://hillaryjunia.github.io/vivu-app-info-pages/introduction.html" // << THAY BẰNG URL THỰC TẾ CỦA BẠN
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("SettingScreen", "Could not open URL: $url", e)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Log out
        SettingItem(
            icon = Icons.Filled.ExitToApp, // Sử dụng Material Icon cho Log out
            title = "Log out",
            iconTint = Color(0xFF00C4B4),
            onClick = { showLogoutDialog = true }
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