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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign // ++ THÊM ++
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.VerifyOtpBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R
@Composable
fun ProfileOTPPassword(
    navController: NavHostController,
    email: String // ++ NHẬN EMAIL TỪ NAVIGATION ++
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

    Log.d("ProfileOTPPassword", "Screen loaded for email: $email")

    // Hàm kiểm tra và gọi API xác thực OTP
    fun verifyOtpAndProceed() {
        if (otpCode.isBlank() || otpCode.length < 6) { // Ví dụ kiểm tra độ dài
            apiError = "Please enter a valid 6-digit OTP code"
            return
        }

        apiError = ""
        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = VerifyOtpBody(
                    email = email,
                    otp = otpCode.trim()
                )
                Log.d("ProfileOTPPassword", "Verifying password reset OTP: $requestBody")
                // Gọi API xác thực OTP quên mật khẩu
                val response = authApiService.verifyPasswordResetOtp(requestBody)

                if (response.isSuccessful) {
                    // Xác thực thành công
                    Log.i("ProfileOTPPassword", "Password reset OTP verified for: $email")
                    Toast.makeText(context, "OTP Verified successfully!", Toast.LENGTH_SHORT).show()

                    // Điều hướng đến màn hình đặt mật khẩu mới, truyền email và otp đã xác thực
                    val verifiedOtp = otpCode.trim()
                    // ++ Đảm bảo route này tồn tại và nhận 2 arguments trong AppNavigation ++
                    navController.navigate("profilePageNewPassword2/$email/$verifiedOtp") {
                        // Tùy chọn popUpTo, ví dụ xóa màn hình nhập email và OTP này
                        popUpTo("profilePageDetailEdit") { inclusive = false } // Quay lại màn hình trước màn hình nhập email
                        // Hoặc popUpTo("profilePageDetail") { inclusive = false } // Quay lại màn hình detail profile
                    }

                } else {
                    // Lỗi từ API
                    val errorBody = response.errorBody()?.string() ?: "Invalid or expired OTP."
                    Log.e("ProfileOTPPassword", "OTP verification failed: ${response.code()} - $errorBody")
                    apiError = "Verification failed: $errorBody"
                    Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Lỗi mạng
                Log.e("ProfileOTPPassword", "OTP verification exception: ${e.message}", e)
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

        // Tiêu đề
        Text(
            text = "You must enter the OTP code sent to your email to change your password.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập OTP code ---
        OutlinedTextField(
            value = otpCode,
            onValueChange = { newValue ->
                if (newValue.length <= 6) {
                    otpCode = newValue.filter { it.isDigit() }
                }
                if (apiError.isNotEmpty()) apiError = ""
            },
            label = { Text("Verification Code") }, // Đổi Label
            placeholder = { Text("Enter 6-digit code", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) verifyOtpAndProceed() }),
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center), // Căn giữa
            isError = apiError.isNotEmpty(),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi API ---
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
            )
        }


        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Next hoặc Loading ---
        Box( /* ... giữ nguyên style Box ... */
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { verifyOtpAndProceed() }, // Gọi hàm xác thực OTP
                    /* ... giữ nguyên style Button ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Verify Code", /* ... */) // Đổi chữ nút
                    // Bỏ icon nếu cần
                }
            }
        }
    }
}