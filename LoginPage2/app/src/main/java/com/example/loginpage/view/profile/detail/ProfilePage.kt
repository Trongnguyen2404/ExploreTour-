package com.example.loginpage.view.profile.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.loginpage.R


@Composable
fun ProfilePage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




        ConstraintLayout {
            val (topImage, profile, textName, infoColumn, settingButton) = createRefs()

            // Ảnh background (được vẽ trước)
            Image(
                painter = painterResource(id = R.drawable.backg_top),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Điều chỉnh chiều cao theo ý muốn
                    .constrainAs(topImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Row chứa IconButton
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(settingButton) { // Áp dụng constrainAs cho Row
                        top.linkTo(parent.top, margin = 48.dp) // Dời xuống như đã yêu cầu trước đó
                        end.linkTo(parent.end)
                    }
            ) {
                IconButton(
                    onClick = { navController.navigate("settingScreen") },
                    modifier = Modifier.padding(end = 16.dp) // Thêm padding để cách mép phải
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "Setting",
                        modifier = Modifier.size(32.dp),

                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.emilia_clarke),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp, 130.dp)
                    .clip(CircleShape)
                    .constrainAs(profile) {
                        top.linkTo(topImage.bottom, margin = (-100).dp) // Điều chỉnh vị trí avatar
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Emilia Clarke",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Thông tin người dùng
        ProfileInfoRow(
            icon = R.drawable.profile_icon,
            label = "NAME",
            value = "Emilia Clarke"
        )

        ProfileInfoRow(
            icon = R.drawable.date_icon,
            label = "DATE OF BIRTH",
            value = "1/1/2001"
        )

        ProfileInfoRow(
            icon = R.drawable.phone_icon,
            label = "MOBILE",
            value = "0987654321"
        )

        ProfileInfoRow(
            icon = R.drawable.mail_icon,
            label = "EMAIL",
            value = "12345@gmail.com"
        )

        ProfileInfoRow(
            icon = R.drawable.lock_icon,
            label = "PASSWORD",
            value = "************"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Header với nút Edit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navController.navigate("profilePageDetail") },
                modifier = Modifier
                    .padding(top = 30.dp)
                    .height(32.dp), // Thu nhỏ chiều cao nút

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Edit",
                    fontSize = 15.sp, // Thu nhỏ chữ
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.navigate_next),
                    contentDescription = "Arrow right",
                    modifier = Modifier
                        .size(30.dp)

                )
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: Int, label: String, value: String) {
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