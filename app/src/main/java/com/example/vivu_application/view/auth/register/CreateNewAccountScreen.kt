package com.example.vivu_application.view.auth.register



import android.util.Log // Thêm import Log
import android.widget.Toast // Thêm import Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext // Thêm import LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.vivu_application.data.model.RequestOtpBody // Import data class
import com.example.vivu_application.data.network.RetrofitClient // Import Retrofit client
import kotlinx.coroutines.launch // Import coroutine launch
import com.example.vivu_application.R

@Composable
fun CreateNewAccountScreen(navController: NavController): Unit {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Thêm trạng thái loading
    val coroutineScope = rememberCoroutineScope() // Scope để chạy coroutine
    val context = LocalContext.current // Lấy Context để hiển thị Toast

    // Lấy instance của API service
    val authApiService = RetrofitClient.authApiService

    fun validateAndProceed() {
        emailError = when {
            email.isBlank() -> "Email cannot be empty"
            !email.lowercase().endsWith("@gmail.com") -> "Email must be a Gmail address (@gmail.com)" // Thêm điều kiện kiểm tra @gmail.com
            else -> "" // Hợp lệ thì xoá lỗi
        }

        if (emailError.isEmpty()) {
            // Nếu email hợp lệ, gọi API
            isLoading = true // Bắt đầu loading
            coroutineScope.launch {
                try {
                    val requestBody = RequestOtpBody(email = email.trim())
                    val response = authApiService.requestOtp(requestBody)

                    if (response.isSuccessful) {
                        val userEmail = email.trim()
                        navController.navigate("createNewAccountOTP/$userEmail") {
                            // popUpTo logic if needed
                        }
                        // }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        // withContext(Dispatchers.Main) {
                        emailError = "Failed to send OTP because the account is exist"
                        // }
                    }
                } catch (e: Exception) {
                    emailError = "Network error or server unavailable. Please check connection" // Thêm tên Exception

                    // }
                } finally {
                    // withContext(Dispatchers.Main) { // Đảm bảo state được cập nhật trên main thread
                    isLoading = false
                    // }
                }
            }
        } else {
            isLoading = false
        }
    }


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
            modifier = Modifier.size(200.dp).padding(top = 40.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))



        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Enter your email to receive an OTP code.", // Thay đổi mô tả
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError.isNotEmpty()) emailError = "" // Xóa lỗi khi người dùng nhập
                },
                placeholder = { Text("Your email", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color(0xFFFFF0F0) // Màu nền khi có lỗi
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Email
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isLoading) validateAndProceed() // Chỉ thực hiện khi không loading
                    }
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 20.sp),
                enabled = !isLoading // Vô hiệu hóa khi đang loading
            )
        }

        // Hiển thị lỗi email bên dưới TextField
        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = Color.Red, // Màu lỗi từ theme
                fontSize = 14.sp, // Giảm cỡ chữ lỗi
                fontWeight = FontWeight.Bold,
                // lineHeight = 18.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp) // Giảm padding
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hiển thị loading indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(20.dp)) // Thêm khoảng cách khi loading
        } else {
            // Chỉ hiển thị Button khi không loading
            Button(
                onClick = {
                    validateAndProceed()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA1C9F1),
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp, // Giảm elevation một chút
                    pressedElevation = 12.dp,
                    disabledElevation = 0.dp
                ),
                enabled = !isLoading // Vô hiệu hóa Button khi đang loading
            ) {
                Text(
                    text = "Next",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }
    }
}