package com.example.loginpage.view.profile.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.loginpage.R

@Composable
fun ProfileEmail(navController: NavHostController) {
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") } // Biến trạng thái lỗi

    val fakepassword = "12345678"

    // Hàm kiểm tra và xử lý dữ liệu
    fun validateAndProceed() {
        when {
            password.isBlank() -> passwordError = "Email cannot be empty"
           password != fakepassword -> passwordError = "Incorrect email"
            else -> {
                passwordError = ""
                navController.navigate("profilePageNewEmail")
            }
        }
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
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Tiêu đề
        Text(
            text = "You must enter your password to change your email",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Ô nhập Email
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Your password", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Email
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        validateAndProceed()
                    }
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 20.sp)
            )
        }

        // Hiển thị lỗi
        Text(
            text = passwordError,
            color = Color.Red,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp, top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Nút Next
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -450.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    validateAndProceed()
                },
                modifier = Modifier
                    .padding(top = 30.dp)
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp, // Đổ bóng đậm
                    pressedElevation = 20.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Text(
                    text = "Next",
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