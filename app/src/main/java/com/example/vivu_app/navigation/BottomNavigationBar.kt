package com.example.vivu_app.navigation

import android.view.WindowInsets
import androidx.benchmark.perfetto.Row
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vivu_app.R
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues





@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", R.drawable.home_icon),
        BottomNavItem("favorites", R.drawable.favorite_icon),
        BottomNavItem("chat", R.drawable.chat_icon),
        BottomNavItem("profile", R.drawable.profile_icon)
    )
    Card (
        modifier = Modifier
            .fillMaxWidth() // Nên dùng fillMaxWidth thay vì width cố định
            .background(Color(0xFFF1E8D9)) // ✅ Chỉ màu taskbar
            .height(65.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1E8D9)),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavigationBar(
                modifier = Modifier
                    // .width(345.dp) // Bỏ width cố định nếu muốn dàn đều
                    .height(50.dp),
                containerColor = Color.Transparent, // Màu nền của NavigationBar trong suốt
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item -> // Không cần forEachIndexed nếu không dùng index nữa
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true // Cách kiểm tra selected tốt hơn cho nested graphs

                    // --- BẮT ĐẦU THAY ĐỔI CHÍNH ---
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val isHovered by interactionSource.collectIsHoveredAsState() // THEO DÕI HOVER

                    // Xác định kích thước mục tiêu cho nền hồng
                    val targetSize = when {
                        isPressed -> 55.dp // Lớn nhất khi nhấn
                        isHovered -> 50.dp // "Bự" khi hover
                        else -> 0.dp      // Ẩn đi khi không tương tác
                    }

                    // Animate kích thước nền
                    val animatedSize by animateDpAsState(
                        targetValue = targetSize,
                        label = "backgroundSizeAnimation"
                        // Có thể thêm animationSpec để tùy chỉnh hiệu ứng
                        // animationSpec = tween(durationMillis = 150)
                    )
                    // --- KẾT THÚC THAY ĐỔI CHÍNH ---

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentDestination?.route != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo("home") { // Đảm bảo "home" là route của màn hình chính xác
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    //restoreState = true // Bạn có thể thử bật lại nếu cần
                                }
                            }
                        },
                        icon = {
                            Box(
                                contentAlignment = Alignment.Center,
                                // Kích thước tổng thể của khu vực icon không thay đổi
                                // để các item không bị nhảy vị trí
                                modifier = Modifier.size(60.dp) // Có thể cần điều chỉnh size này
                            ) {
                                // 1. Vẽ nền hồng với kích thước động
                                //    Nó sẽ được vẽ ĐẰNG SAU icon và thanh ngang
                                if (animatedSize > 0.dp) { // Chỉ vẽ khi có kích thước
                                    Box(
                                        modifier = Modifier
                                            .size(animatedSize) // SỬ DỤNG KÍCH THƯỚC ANIMATED
                                            .clip(CircleShape)
                                            .background(Color(0xFFF4C2D7)) // Màu hồng
                                    )
                                }

                                // 2. Vẽ Icon và thanh ngang (logic giữ nguyên)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Image(
                                        painter = painterResource(id = item.icon),
                                        contentDescription = item.route,
                                        modifier = Modifier.size(25.dp),
                                        colorFilter = ColorFilter.tint(
                                            if (isSelected) Color.Black else Color(0xFF666666)
                                        )
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 5.dp)
                                                .width(30.dp)
                                                .height(3.dp)
                                                .background(Color.Black)
                                        )
                                    }
                                }
                            }
                        },
                        // Quan trọng: Truyền interactionSource vào đây để nó hoạt động
                        interactionSource = interactionSource,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black, // Màu icon được quản lý bởi ColorFilter ở trên
                            unselectedIconColor = Color(0xFF666666), // Màu icon được quản lý bởi ColorFilter ở trên
                            indicatorColor = Color.Transparent // Rất quan trọng để ẩn indicator mặc định
                        )
                    )
                }
            }
        }
    }
}