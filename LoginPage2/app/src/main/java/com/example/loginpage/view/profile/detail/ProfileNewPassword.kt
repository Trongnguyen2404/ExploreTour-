package com.example.loginpage.view.profile.detail


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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
fun ProfileNewPassword(navController: NavHostController) {
    var repassword by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    fun validateAndProceed(){
        when {
            password.isBlank() -> passwordError = "Password cannot be empty "
            repassword.isBlank() -> passwordError = "Please confirm your password "
            password != repassword -> passwordError = "Password do not the same "
            else -> {
                passwordError = ""
                navController.popBackStack("profilePageDetail", inclusive = false)
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
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "",
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Tiêu đề
        Text(
            text = "You need to enter a new password",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Ô nhập New Password
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)), // Cắt nền theo viền bo tròn
                shape = RoundedCornerShape(12.dp), // Bo tròn khung
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray, // Màu viền khi focus
                    unfocusedIndicatorColor = Color.Gray, // Màu viền khi không focus
                    focusedContainerColor = Color.White, // Nền khi focus
                    unfocusedContainerColor = Color.White // Nền khi không focus
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, // Chuyển sang trường tiếp theo khi nhấn Enter
                    keyboardType = KeyboardType.Password // Bàn phím cho mật khẩu
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down) // Chuyển focus xuống dưới
                    }
                ),
                singleLine = true, // Ngăn xuống dòng
                textStyle = TextStyle(fontSize = 20.sp) // Tăng cỡ chữ trong khung nhập
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Ô nhập Re-NewPassword
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = repassword,
                onValueChange = { repassword = it },
                placeholder = { Text("Re password", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)), // Cắt nền theo viền bo tròn
                shape = RoundedCornerShape(12.dp), // Bo tròn khung
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray, // Màu viền khi focus
                    unfocusedIndicatorColor = Color.Gray, // Màu viền khi không focus
                    focusedContainerColor = Color.White, // Nền khi focus
                    unfocusedContainerColor = Color.White // Nền khi không focus
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Hoàn thành khi nhấn Enter
                    keyboardType = KeyboardType.Password // Bàn phím cho mật khẩu
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        validateAndProceed() // gọi hàm khi nhấn enter
                    }
                ),
                singleLine = true, // Ngăn xuống dòng
                textStyle = TextStyle(fontSize = 20.sp) // Tăng cỡ chữ trong khung nhập
            )
        }

        Text(
            text = passwordError, // Hiển thị lỗi nếu có
            color = Color.Red,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        // Nút Next
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    validateAndProceed()
                },
                modifier = Modifier
                    .padding(top = 30.dp)
                    .height(32.dp), // Thu nhỏ chiều cao nút

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp, // Độ cao mặc định tạo bóng
                    pressedElevation = 20.dp, // Độ cao khi nhấn
                    disabledElevation = 0.dp // Độ cao khi vô hiệu hóa
                )
            ) {
                Text(
                    text = "Complete",
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