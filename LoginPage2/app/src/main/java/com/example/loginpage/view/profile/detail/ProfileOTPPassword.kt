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
fun ProfileOTPPassword(navController: NavHostController) {
    var otpcode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf("") }

    val fakeotp = "1111"

    // Hàm kiểm tra và xử lý dữ liệu
    fun validateAndProceed() {
        when {
            otpcode.isBlank() -> showError = "OTP code cannot be empty"
            otpcode != fakeotp -> showError = "Incorrect OTP code"
            else -> {
                showError = ""
                navController.navigate("profilePageNewPassword")
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
            text = "You must enter the OTP code sent to your email to change your password.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Ô nhập OTP code
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = otpcode   ,
                onValueChange = { otpcode= it },
                placeholder = { Text("OTP code", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)), // Cắt nền theo viền bo tròn
                shape = RoundedCornerShape(12.dp), // Bo tròn khung
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray, // Màu viền khi focus
                    unfocusedIndicatorColor = Color.Gray, // Màu viền khi không focus
                    focusedContainerColor = Color.White, // Nền khi focus
                    unfocusedContainerColor = Color.White // Nền khi không focus
                ),keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Hành động khi nhấn Enter
                    keyboardType = KeyboardType.Email // Đảm bảo bàn phím phù hợp với email
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        validateAndProceed()
                    }
                ),
                singleLine = true, // Ngăn xuống dòng
                textStyle = TextStyle(fontSize = 20.sp) // Tăng cỡ chữ trong khung nhập
            )
        }


        Text(
            text = (showError),
            fontSize = 15.sp,
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
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
                    .height(32.dp), // Thu nhỏ chiều cao nút

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 16.dp, // Đổ bóng đậm
                    pressedElevation = 20.dp,
                    disabledElevation = 0.dp
                )
            )  {
                Text(
                    text = "Next",
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