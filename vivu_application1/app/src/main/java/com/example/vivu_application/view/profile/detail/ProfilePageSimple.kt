package com.example.vivu_application.view.profile.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vivu_application.navigation.BottomNavigationBar
import com.example.vivu_application.R
@Composable
fun ProfilePageSimple(navController: NavController) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // Thêm padding theo insets hệ thống
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        containerColor = Color.Transparent,
        bottomBar = { BottomNavigationBar(navController) },
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phần đầu: Ảnh nền và nút Setting (không áp dụng innerPadding)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .windowInsetsPadding(WindowInsets(top = 0.dp)) // Bỏ padding trên cùng
            ) {
                // Ảnh nền
                Image(
                    painter = painterResource(id = R.drawable.backg_top),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )

                // Nút Setting ở góc trên bên phải
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, end = 16.dp)
                ) {
                    IconButton(
                        onClick = { navController.navigate("settingScreen") }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Setting",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Black
                        )
                    }
                }

                // Ảnh hồ sơ nằm giữa và chồng lên ảnh nền
                ProfileImage1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 20.dp)
                )
            }

            // Nội dung còn lại áp dụng innerPadding để tránh bị che bởi BottomNavigationBar
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Emilia Clarke",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Thông tin người dùng
                ProfileInfoRow2(
                    icon = R.drawable.profile_icon,
                    label = "NAME",
                    value = "Emilia Clarke"
                )

                ProfileInfoRow2(
                    icon = R.drawable.date_icon,
                    label = "DATE OF BIRTH",
                    value = "1/1/2001"
                )

                ProfileInfoRow2(
                    icon = R.drawable.phone_icon,
                    label = "MOBILE",
                    value = "0987654321"
                )

                ProfileInfoRow2(
                    icon = R.drawable.mail_icon,
                    label = "EMAIL",
                    value = "12345@gmail.com"
                )

                ProfileInfoRow2(
                    icon = R.drawable.lock_icon,
                    label = "PASSWORD",
                    value = "************"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Edit
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { navController.navigate("profilePageDetail") },
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,
                            pressedElevation = 5.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "Edit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.navigate_next),
                            contentDescription = "Arrow right",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow2(icon: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProfileImage2(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(140.dp)) // Giảm Spacer để nâng ảnh hồ sơ lên
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)

        ) {
            Image(
                painter = if (imageUri == null) painterResource(R.drawable.emilia_clarke)
                else rememberAsyncImagePainter(imageUri),
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}