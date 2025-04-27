package com.example.vivu_application.view.profile.detail


import android.util.Log // ++ THÊM ++
import android.widget.Toast // ++ THÊM ++
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ++ THÊM ++
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // ++ THÊM ++
import androidx.compose.ui.platform.LocalFocusManager // ++ THÊM ++
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign // ++ THÊM ++
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.VerifyEmailChangeBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R
@Composable
fun ProfileOTPEmail(
    navController: NavHostController,
    newEmail: String // ++ NHẬN EMAIL MỚI TỪ NAVIGATION ++
) {
    var otpCode by remember { mutableStateOf("") } // Đổi tên biến
    // var showError by remember { mutableStateOf("") } // Bỏ lỗi cũ
    var apiError by remember { mutableStateOf("") } // Lỗi API
    var isLoading by remember { mutableStateOf(false) } // State loading

    // val fakeotp = "1111" // Xóa OTP giả

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Log.d("ProfileOTPEmail", "Screen loaded for new email: $newEmail")

    // Hàm kiểm tra và gọi API
    fun verifyOtpAndComplete() {
        if (otpCode.isBlank() || otpCode.length < 6) { // Ví dụ kiểm tra độ dài OTP
            apiError = "Please enter a valid 6-digit OTP code"
            return
        }

        apiError = ""
        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = VerifyEmailChangeBody(
                    newEmail = newEmail, // Dùng email mới nhận được
                    otp = otpCode.trim()
                )
                Log.d("ProfileOTPEmail", "Verifying email change OTP: $requestBody")
                val response = authApiService.verifyEmailChange(requestBody)

                if (response.isSuccessful) {
                    // Xác thực thành công, email đã được thay đổi ở backend
                    Log.i("ProfileOTPEmail", "Email change verified successfully for: $newEmail")
                    Toast.makeText(context, "Email changed successfully!", Toast.LENGTH_LONG).show()

                    // Điều hướng về màn hình profile chính hoặc màn hình trước đó
                    // Xóa các màn hình của luồng đổi email khỏi back stack
                    navController.navigate("profilePageDetailEdit") { // Hoặc "profilePage", "settingScreen" tùy đích đến
                        launchSingleTop = true
                    }
                    // Quan trọng: Cần có cơ chế cập nhật lại UI ở màn hình profile để hiển thị email mới

                } else {
                    // Lỗi xác thực OTP từ API
                    val errorBody = response.errorBody()?.string() ?: "Invalid or expired OTP."
                    Log.e("ProfileOTPEmail", "Email change verification failed: ${response.code()} - $errorBody")
                    apiError = "Verification failed: $errorBody"
                    Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi khác
                Log.e("ProfileOTPEmail", "Email change verification exception: ${e.message}", e)
                apiError = "Network error or server unavailable."
                Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
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

        Text(
            text = "A code has been sent to:\n$newEmail",
            fontSize = 16.sp, color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập OTP code ---
        OutlinedTextField(
            value = otpCode,
            onValueChange = { newValue ->
                if (newValue.length <= 6) {
                    otpCode = newValue.filter { it.isDigit() }
                }
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi khi nhập
            },
            label = { Text("Verification Code") }, // Đổi label
            placeholder = { Text("Enter 6-digit code", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... your colors ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) verifyOtpAndComplete() }),
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
            isError = apiError.isNotEmpty(),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi API ---
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError, // Hiển thị lỗi từ state apiError
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Confirm hoặc Loading ---
        Box( /* ... giữ nguyên style Box ... */
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { verifyOtpAndComplete() }, // Gọi hàm xác thực
                    /* ... giữ nguyên style Button ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Confirm", /* ... style chữ ... */) // Đổi chữ nút
                    // Bỏ icon nếu muốn
                }
            }
        }
    }
}