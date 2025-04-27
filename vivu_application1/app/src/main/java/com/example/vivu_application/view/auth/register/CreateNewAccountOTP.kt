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
import androidx.compose.ui.platform.LocalContext // Thêm import LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.VerifyOtpBody // ++ THÊM IMPORT NÀY ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM IMPORT NÀY ++
import kotlinx.coroutines.launch // ++ THÊM IMPORT NÀY ++
import com.example.vivu_application.R

@Composable
fun CreateNewAccountOTP(
    navController: NavHostController,
    email: String // ++ THÊM THAM SỐ EMAIL ++
) {
    var otpCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // ++ THÊM TRẠNG THÁI LOADING ++
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope() // ++ THÊM COROUTINE SCOPE ++
    val context = LocalContext.current // ++ THÊM CONTEXT ++
    val authApiService = RetrofitClient.authApiService // ++ LẤY API SERVICE ++

    // Xóa fakeOtp, vì giờ sẽ gọi API
    // val fakeOtp = "1111"

    fun validateAndProceed() {
        // Chỉ kiểm tra cơ bản xem OTP có rỗng không
        if (otpCode.isBlank()) {
            showError = "OTP code cannot be empty"
            isLoading = false // Đảm bảo không loading nếu lỗi ngay
            return // Dừng hàm ở đây
        }

        // Nếu không rỗng, xóa lỗi cũ (nếu có) và gọi API
        showError = ""
        isLoading = true
        focusManager.clearFocus() // Xóa focus khỏi TextFieldD

        coroutineScope.launch {
            try {
                val requestBody = VerifyOtpBody(email = email, otp = otpCode.trim())
                val response = authApiService.verifyOtp(requestBody)

                if (response.isSuccessful) {
                    // OTP hợp lệ
                    Log.i("VerifyOTP", "OTP verification successful for email: $email")
                    Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()

                    // ++ ĐẢM BẢO BẠN LÀM NHƯ THẾ NÀY ++
                    // Lấy chính OTP mà người dùng vừa nhập và đã được xác thực
                    val verifiedOtp = otpCode.trim()
                    Log.d("VerifyOTP", "Navigating to createNameAndPasswordAccount with email: $email and OTP: $verifiedOtp") // Thêm log

                    // Điều hướng và TRUYỀN CẢ email VÀ verifiedOtp
                    navController.navigate("createNameAndPasswordAccount/$email/$verifiedOtp") {
                        // Tùy chọn: Xóa màn hình OTP khỏi back stack
                        popUpTo("createAccountScreen") { inclusive = false } // Giữ lại màn hình nhập email
                        // Hoặc popUpTo("createNewAccountOTP/{email}") { inclusive = true } // Xóa màn hình OTP (cẩn thận argument)
                    }
                    // -- KẾT THÚC PHẦN QUAN TRỌNG --

                } else {
                    // ... xử lý lỗi xác thực OTP ...
                    val errorBody = response.errorBody()?.string() ?: "Invalid OTP or server error"
                    Log.e("VerifyOTP", "OTP verification failed: ${response.code()} - $errorBody")
                    showError = "Verification failed: $errorBody"
                    Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi không xác định
                Log.e("VerifyOTP", "OTP verification exception: ${e.message}", e)
                showError = "Network error or server unavailable. Please check connection."
                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false // Kết thúc loading
            }
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
            painter = painterResource(id = R.drawable.group_1), // Đảm bảo hình ảnh này tồn tại
            contentDescription = "OTP Verification Illustration",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Verify OTP", // Đổi tiêu đề
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp)) // Giảm khoảng cách

        // Hiển thị email đang xác thực (Tùy chọn)
        Text(
            text = "Enter the OTP sent to:\n$email",
            fontSize = 18.sp, // Cỡ chữ nhỏ hơn
            // fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = otpCode,
            onValueChange = { newValue ->
                // Giới hạn độ dài OTP nếu cần (ví dụ: 6 ký tự)
                if (newValue.length <= 6) {
                    otpCode = newValue.filter { it.isDigit() } // Chỉ cho phép nhập số
                }
                if (showError.isNotEmpty()) showError = "" // Xóa lỗi khi người dùng nhập
            },
            placeholder = { Text("Enter OTP", color = Color.Gray) }, // Đổi placeholder
            modifier = Modifier.fillMaxWidth(),
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
                keyboardType = KeyboardType.NumberPassword // Dùng NumberPassword để ẩn số nếu muốn, hoặc Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!isLoading) validateAndProceed() // Chỉ thực hiện khi không loading
                }
            ),
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center), // Căn giữa text OTP
            isError = showError.isNotEmpty(), // Đánh dấu lỗi
            enabled = !isLoading // Vô hiệu hóa khi loading
        )

        // Hiển thị lỗi OTP
        if (showError.isNotEmpty()) {
            Text(
                text = showError,
                color = MaterialTheme.colorScheme.error, // Màu lỗi từ theme
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hiển thị loading hoặc Button
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Button(
                onClick = { validateAndProceed() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA1C9F1),
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp,
                    disabledElevation = 0.dp
                ),
                enabled = !isLoading // Vô hiệu hóa khi loading
            ) {
                Text(
                    text = "Verify OTP", // Đổi text Button
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }
    }
}