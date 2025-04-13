// ForgotPasswordScreen.kt
package com.example.loginpage.view.auth.login
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
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
fun PasswordRecoveryOTP(navController: NavHostController) {
    var otpcode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val fakeotp = "1111"

    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFFFFFFF),
                        0.78f to Color(0xFF72DADE),
                        1.0f to Color(0xFFA2C9F0)
                    )
                )
            )
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_1),
            contentDescription = "Login image",
            modifier = Modifier.size(200.dp)
        )


        Spacer(modifier = Modifier.height(40.dp))


        Text(
            text = "Password recovery",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Hành động khi nhấn Enter
                    keyboardType = KeyboardType.Email // Đảm bảo bàn phím phù hợp với email
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Logic khi nhấn Enter
                        if (otpcode == fakeotp) {
                            navController.navigate("newPasswordScreen")
                            showError = false // ẩn lỗi khi email đúng
                        } else {
                            showError = true // hiện lỗi khi email sai
                        }
                    }
                ),
            singleLine = true, // Ngăn xuống dòng
            textStyle = TextStyle(fontSize = 20.sp) // Tăng cỡ chữ trong khung nhập
            )
        }

        Text(
            text = if (showError) "OTP is not correct" else "",
            fontSize = 15.sp,
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )


        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if(otpcode == fakeotp){
                    navController.navigate("newPasswordScreen")
                    showError = false // ẩn lỗi khi email đúng
                } else {
                    showError = true // hiện lỗi khi email sai
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA1C9F1), // Màu nền của button
                contentColor = Color.Black // Màu chữ trên button
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 16.dp, // Độ cao mặc định tạo bóng
                pressedElevation = 20.dp, // Độ cao khi nhấn
                disabledElevation = 0.dp // Độ cao khi vô hiệu hóa
            )
        ) {
            Text(
                text = "Confirm",
                style = TextStyle(fontSize = 20.sp) // Cỡ chữ của text trong button
            )
        }


    }
}