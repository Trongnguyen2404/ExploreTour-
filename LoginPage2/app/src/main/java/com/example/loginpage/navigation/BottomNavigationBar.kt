package com.example.loginpage.navigation


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.loginpage.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", R.drawable.home_icon),
        BottomNavItem("favorites", R.drawable.favorite_icon),
        BottomNavItem("chat", R.drawable.chat_icon),
        BottomNavItem("profilePageDetail", R.drawable.profile_icon)
    )

    Card(
        modifier = Modifier
            .width(440.dp)  //  Giữ nguyên chiều rộng Taskbar
            .height(65.dp)  //  Giữ nguyên chiều cao Taskbar
            .clip(RoundedCornerShape(50.dp)), //  Bo tròn góc
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1E8D9)), // Màu nền be
        elevation = CardDefaults.cardElevation(6.dp) //  Bóng đổ nhẹ

    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavigationBar(
                modifier = Modifier
                    .width(345.dp) //  Giới hạn vùng chứa icon
                    .height(50.dp),
                containerColor = Color.Transparent, //  Tránh chồng màu lên Taskbar
                tonalElevation = 0.dp //  Bỏ hiệu ứng nổi
            ) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(30.dp) //  Giữ vùng tap hợp lý
                                    .offset(y = 5.dp), //  Hạ icon xuống một chút
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.route,
                                    modifier = Modifier.size(25.dp) //  Icon to hơn một chút để cân đối

                                )
                            }
                        },
                        selected = currentRoute == item.route,
                        onClick = { navController.navigate(item.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                    // Thêm khoảng cách giữa các icon, nhưng không thêm sau icon cuối
                    if (index < items.size - 1) {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                }
            }
        }
    }
}